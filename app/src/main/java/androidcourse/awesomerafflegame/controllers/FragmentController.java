package androidcourse.awesomerafflegame.controllers;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.lang.ref.WeakReference;

import androidcourse.awesomerafflegame.activities.MainActivity;
import androidcourse.awesomerafflegame.R;

/**
 * Created by Jesper on 04/04/16.
 */
public class FragmentController {

    private static FragmentController controller;

    public static FragmentController get(){
        if(controller == null) {
            controller = new FragmentController();
        }
        return controller;
    }

    private FragmentController() {
    }

    public void setToolbarTitle(String title) {
        MainActivity.toolbar.setTitle(title);
    }

    public void clearBackStack(MainActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void transactFragments(FragmentActivity fragmentActivity, Fragment fragment, String backStackTag) {
        WeakReference<FragmentActivity> wrActivity = new WeakReference<>(fragmentActivity);
        final Activity activity = wrActivity.get();
        if(activity != null && !activity.isFinishing()) {
            if (fragment != null) {
                FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(backStackTag != null) {
                    fragmentTransaction.addToBackStack(backStackTag);
                }
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }
}
