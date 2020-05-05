package org.smartregister.growthmonitoring.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.growthmonitoring.R;
import org.smartregister.growthmonitoring.util.ImageUtils;

/**
 * Created by ndegwamartin on 2020-03-23.
 */
public class ImageUtilsTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void assertProfileImageResourceByGenderWithEmptyStringParameterReturnsDefaultResource() {
        Assert.assertEquals(ImageUtils.profileImageResourceByGender(""), R.drawable.child_boy_infant);
    }

    @Test
    public void assertProfileImageResourceByGenderWithMaleParameterReturnsMaleResource() {
        Assert.assertEquals(ImageUtils.profileImageResourceByGender("male"), R.drawable.child_boy_infant);
    }

}
