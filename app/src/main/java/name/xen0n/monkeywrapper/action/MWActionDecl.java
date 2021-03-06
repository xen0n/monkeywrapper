package name.xen0n.monkeywrapper.action;

import java.lang.reflect.InvocationTargetException;

import name.xen0n.monkeywrapper.action.bridge.MonkeyBridge;
import name.xen0n.monkeywrapper.action.sequence.MWActionSequence;
import name.xen0n.monkeywrapper.action.sequence.MWSequenceFactory;
import android.text.TextUtils;


public class MWActionDecl {

    private final int targetActionId;
    private final CharSequence targetPackage;
    private final CharSequence targetClass;
    private final Class<? extends MWSequenceFactory> factoryClass;

    protected MWActionDecl(
            final int targetActionId,
            final CharSequence targetPackage,
            final CharSequence targetClass,
            final Class<? extends MWSequenceFactory> factoryClass) {
        this.targetActionId = targetActionId;
        this.targetPackage = targetPackage;
        this.targetClass = targetClass;
        this.factoryClass = factoryClass;
    }

    public boolean matchTarget(
            final int actionId,
            final CharSequence packageName,
            final CharSequence className) {
        if (targetActionId != actionId) {
            return false;
        }

        if (TextUtils.isEmpty(packageName)) {
            if (TextUtils.isEmpty(targetClass)) {
                return true;
            }
            return targetClass.equals(className);
        }

        if (!targetPackage.equals(packageName)) {
            return false;
        }

        if (TextUtils.isEmpty(targetClass)) {
            return true;
        }

        return targetClass.equals(className);
    }

    public int sendToBridge(final MonkeyBridge bridge) {
        return getSequence().sendToBridge(bridge);
    }

    private MWActionSequence getSequence() {
        MWSequenceFactory factory;
        try {
            factory = factoryClass.getConstructor().newInstance();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return factory.getSequence();
    }
}
