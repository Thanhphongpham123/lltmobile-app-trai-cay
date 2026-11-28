package com.example.foodappproject;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProfilePersistenceTest {
    @Test
    public void editedProfileSurvivesAccountSwitch() {
        String prefix = UUID.randomUUID().toString();
        Context context = new ContextWrapper(InstrumentationRegistry.getInstrumentation().getTargetContext()) {
            @Override
            public SharedPreferences getSharedPreferences(String name, int mode) {
                return super.getSharedPreferences(prefix + name, mode);
            }
        };
        assertEquals("Phạm Ngọc Thành Phong", FoodDataManager.currentProfile(context)
                .getString(profile_activity.KEY_NAME, ""));
        FoodDataManager.registerAccount(context, "An", "an@example.com", "", "pass-a");
        assertTrue(FoodDataManager.authenticateCustomer(context, "an@example.com", "pass-a"));
        FoodDataManager.currentProfile(context).edit()
                .putString(profile_activity.KEY_NAME, "An Nguyen")
                .putString(profile_activity.KEY_ADDRESS, "Dia chi A")
                .putString(profile_activity.KEY_EMAIL, "contact@example.com").apply();
        FoodDataManager.registerAccount(context, "Binh", "binh@example.com", "", "pass-b");
        assertTrue(FoodDataManager.authenticateCustomer(context, "binh@example.com", "pass-b"));
        assertFalse(FoodDataManager.currentProfile(context).contains(profile_activity.KEY_ADDRESS));
        assertTrue(FoodDataManager.authenticateCustomer(context, "AN@example.com", "pass-a"));
        SharedPreferences profile = FoodDataManager.currentProfile(context);
        assertEquals("An Nguyen", profile.getString(profile_activity.KEY_NAME, ""));
        assertEquals("Dia chi A", profile.getString(profile_activity.KEY_ADDRESS, ""));
        assertEquals("contact@example.com", profile.getString(profile_activity.KEY_EMAIL, ""));
    }
}
