package solutions.alterego.androidbound.android.viewresolvers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.android.interfaces.IViewResolver;

public class ChainedViewResolver implements IViewResolver {

    private List<IViewResolver> mBaseViewResolvers;

    public ChainedViewResolver() {
        mBaseViewResolvers = new ArrayList<>();
    }

    public ChainedViewResolver(IViewResolver... initialViewResolvers) {
        this();
        if (initialViewResolvers == null) {
            return;
        }

        mBaseViewResolvers.addAll(Arrays.asList(initialViewResolvers));
    }

    @Override
    public void setLogger(ILogger logger) {
    }

    @Override
    public View createView(String name, Context context, AttributeSet attrs) {
        for (IViewResolver resolver : mBaseViewResolvers) {
            View view = resolver.createView(name, context, attrs);
            if (view != null) {
                return view;
            }
        }

        return null;
    }

    public void addResolverToFront(IViewResolver resolver) {
        mBaseViewResolvers.add(0, resolver);
    }

    public void addResolverToBack(IViewResolver resolver) {
        mBaseViewResolvers.add(resolver);
    }

    public void removeResolver(IViewResolver resolver) {
        mBaseViewResolvers.remove(resolver);
    }

    @Override
    public void dispose() {
        mBaseViewResolvers.clear();
    }
}
