package crazycourier.oddymobstar.crazycourier.fragment;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import oddymobstar.activity.DemoActivity;

/**
 * Created by timmytime on 05/05/15.
 */
public class FragmentTest extends ActivityInstrumentationTestCase2<DemoActivity> {

    private DemoActivity activity;


    public FragmentTest() {
        super(DemoActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();

        activity = getActivity();

    }

    @UiThreadTest
    public void testMenu() {

        /*
        i really CBA to have to test all this shit out.
        im not convinced of the point of it.  i mean its like test the layout weighting not changed
        it could take fucking ages...thats why you code review.
        also half the shit cant be tested anyway....as it needs the server etc.
         */


    }


    /*

    this is where it gets harder...

    do we a: test menu buttons?

     */


}
