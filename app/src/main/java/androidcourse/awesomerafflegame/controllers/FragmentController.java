package androidcourse.awesomerafflegame.controllers;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.lang.ref.WeakReference;

import androidcourse.awesomerafflegame.activities.MainActivity;
import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.fragments.StartFragment;

/**
 * Created by Jesper on 04/04/16.
 */
public class FragmentController {

    private static FragmentController controller;

    public static FragmentController get() {
        if (controller == null) {
            controller = new FragmentController();
        }
        return controller;
    }

    private FragmentController() {
    }

    public void setToolbarTitle(String title) {
        MainActivity.toolbar.setTitle(title);
    }

    public void returnToHome(Activity activity) {
        FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();

        // First, clear back stack
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        transactFragments(activity, new StartFragment(), "start_fragment");
    }

    public void transactDialogFragment(FragmentActivity fragmentActivity, DialogFragment fragment, String backStackTag) {
        if (fragment != null) {
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            if (backStackTag != null) {
                fragment.show(fragmentManager, backStackTag);
            } else {
                fragment.show(fragmentManager, "");
            }
        }
    }

    public void transactFragments(Activity activity, Fragment fragment, String backStackTag) {
        if (fragment != null) {
            FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (backStackTag != null) {
                fragmentTransaction.addToBackStack(backStackTag);
            }
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
}
