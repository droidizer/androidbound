package solutions.alterego.androidbound.binding.types;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.helpers.Reflector;
import solutions.alterego.androidbound.helpers.reflector.PropertyInfo;
import solutions.alterego.androidbound.interfaces.ILogger;

public class PropertyBinding extends BindingBase {

    private Disposable mMemberDisposable;

    private PropertyInfo mPropertyInfo;

    public PropertyBinding(Object subject, String propertyName, boolean needChangesIfPossible, ILogger logger) {
        super(subject, logger);

        mPropertyInfo = Reflector.getProperty(subject.getClass(), propertyName, logger);
        setupBinding(subject, mPropertyInfo.getPropertyName(), needChangesIfPossible);
    }

    private void setupBinding(Object subject, final String propertyName, boolean needChangesIfPossible) {
        if (subject == null) {
            return;
        }

        if (needChangesIfPossible && (getSubject() instanceof INotifyPropertyChanged)) {
            setupChanges(true);
            getLogger().debug(propertyName + " implements INotifyPropertyChanged. Subscribing...");

            mMemberDisposable = ((INotifyPropertyChanged) subject).onPropertyChanged()
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String member) throws Exception {
                            return member.equals(propertyName);
                        }
                    })
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            onBoundPropertyChanged();
                        }
                    });
        } else {
            setupChanges(false);
        }
    }

    protected PropertyInfo getInfo() {
        return mPropertyInfo;
    }

    protected void onBoundPropertyChanged() {
        notifyChange(getValue());
    }

    @Override
    public Class<?> getType() {
        return mPropertyInfo.getPropertyType();
    }

    @Override
    public Object getValue() {
        if (mPropertyInfo.isCanRead()) {
            return mPropertyInfo.getValue(getSubject());
        }

        getLogger().warning(
                "Cannot get value for property " + mPropertyInfo.getPropertyName() + ": property is non-existent");
        return noValue;
    }

    @Override
    public void setValue(Object value) {
        if (mPropertyInfo.isCanWrite()) {
            mPropertyInfo.setValue(getSubject(), value);
        } else {
            if (mPropertyInfo.isCanRead()) {
                getLogger().warning(
                        "Cannot set value for property " + mPropertyInfo.getPropertyName() + ": propery is read-only");
            } else {
                getLogger().warning("Cannot set value for property " + mPropertyInfo.getPropertyName()
                        + ": propery is non-existent");
            }
        }
    }

    @Override
    public void addValue(Object object) {
        if (mPropertyInfo.isCanAdd()) {
            mPropertyInfo.addValue(getSubject(), object);
        } else {
            if (mPropertyInfo.isCanRead()) {
                getLogger().warning(
                        "Cannot add value for property " + mPropertyInfo.getPropertyName() + ": propery is read-only");
            } else {
                getLogger().warning("Cannot add value for property " + mPropertyInfo.getPropertyName()
                        + ": propery is non-existent");
            }
        }
    }

    @Override
    public void removeValue(Object result) {
        if (mPropertyInfo.isCanRemove()) {
            mPropertyInfo.removeValue(getSubject(), result);
        } else {
            if (mPropertyInfo.isCanRead()) {
                getLogger().warning(
                        "Cannot add value for property " + mPropertyInfo.getPropertyName() + ": propery is read-only");
            } else {
                getLogger().warning("Cannot add value for property " + mPropertyInfo.getPropertyName()
                        + ": propery is non-existent");
            }
        }
    }


    @Override
    public void dispose() {
        if (mMemberDisposable != null) {
            mMemberDisposable.dispose();
            mMemberDisposable = null;
        }
        super.dispose();
    }
}
