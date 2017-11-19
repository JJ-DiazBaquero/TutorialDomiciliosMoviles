package com.moviles.domiciliosmoviles;


import android.os.SystemClock;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import com.moviles.domiciliosmoviles.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PedidoActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() throws InterruptedException {
    SystemClock.sleep(3000);
    ViewInteraction relativeLayout = onView(
            allOf(childAtPosition(
                    withId(R.id.platos_listview),
                    3),
                    isDisplayed()));
        relativeLayout.perform(click());
    }

    @Test
    public void pedidoActivityTest() {

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.mas), withText("+1000"),
                        withParent(allOf(withId(R.id.fragment_propina),
                                withParent(withId(R.id.content_pedidos)))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.propina_text), withText("$1000"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_propina),
                                        childAtPosition(
                                                withId(R.id.content_pedidos),
                                                4)),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("$2000")));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.menos), withText("-1000"),
                        withParent(allOf(withId(R.id.fragment_propina),
                                withParent(withId(R.id.content_pedidos)))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.propina_text), withText("$0"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_propina),
                                        childAtPosition(
                                                withId(R.id.content_pedidos),
                                                4)),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("$1000")));

    }

    @Test
    public void titlePedidoTest() {
        onView(allOf(withParent(withId(R.id.toolbar)), withText("Combo hamburguesa")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void metodoDePagoTest() {
        ViewInteraction metodoButton = onView(allOf(withId(R.id.payment_method)));
        metodoButton.perform(scrollTo(), click());

        ViewInteraction radioButton = onView(allOf(withId(R.id.credito_btn),
                withText("Tarjeta de Credito")));

        radioButton.perform(click());

        ViewInteraction selectButton =   onView(allOf(withId(R.id.select_btn),
                withText("Seleccionar"), isDisplayed()));

        selectButton.perform(click());

        metodoButton.check(matches(withText("Tarjeta de Credito")));
    }

    @Test
    public void realizarPedidoTest() throws InterruptedException {
        ViewInteraction nombreText = onView(withId(R.id.nombre_cliente));
        nombreText.perform(scrollTo(), replaceText("Mario Linares"), closeSoftKeyboard());

        ViewInteraction lugarText = onView(withId(R.id.lugar_pedido));
        lugarText.perform(scrollTo(), replaceText("ML 652"), closeSoftKeyboard());

        ViewInteraction metodoButton = onView(allOf(withId(R.id.payment_method)));
        metodoButton.perform(scrollTo(), click());

        ViewInteraction radioButton = onView(allOf(withId(R.id.credito_btn)));
        radioButton.perform(click());

        ViewInteraction selectButton = onView(allOf(withId(R.id.select_btn)));
        selectButton.perform(click());

        ViewInteraction propinaButton = onView(allOf(withId(R.id.mas)));
        propinaButton.perform(scrollTo(), click());

        ViewInteraction pedirButton = onView(allOf(withId(R.id.pedir_btn)));
        pedirButton.perform(scrollTo(), click());

        Thread.sleep(5000);

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Su pedido fue registrado"))).check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
