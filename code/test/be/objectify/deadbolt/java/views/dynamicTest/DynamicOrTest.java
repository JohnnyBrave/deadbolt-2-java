package be.objectify.deadbolt.java.views.dynamicTest;

import be.objectify.deadbolt.java.AbstractDynamicResourceHandler;
import be.objectify.deadbolt.java.AbstractFakeApplicationTest;
import be.objectify.deadbolt.java.AbstractNoPreAuthDeadboltHandler;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.cache.HandlerCache;
import org.junit.Assert;
import org.junit.Test;
import play.mvc.Http;
import play.test.Helpers;
import play.twirl.api.Content;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class DynamicOrTest extends AbstractFakeApplicationTest
{
    @Test
    public void testValid()
    {
        final DeadboltHandler deadboltHandler = new AbstractNoPreAuthDeadboltHandler()
        {
            @Override
            public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context)
            {
                return CompletableFuture.supplyAsync(() -> Optional.of(new AbstractDynamicResourceHandler()
                {
                    @Override
                    public CompletionStage<Boolean> isAllowed(final String name,
                                                        final String meta,
                                                        final DeadboltHandler deadboltHandler,
                                                        final Http.Context ctx)
                    {
                        return CompletableFuture.completedFuture(true);
                    }
                }));
            }
        };
        final Content html = be.objectify.deadbolt.java.views.html.dynamicTest.dynamicOrContent.render("foo",
                                                                                                       "bar",
                                                                                                       deadboltHandler);
        final String content = Helpers.contentAsString(html);
        Assert.assertTrue(content.contains("This is before the constraint."));
        Assert.assertTrue(content.contains("This is protected by the constraint."));
        Assert.assertFalse(content.contains("This is default content in case the constraint denies access to the protected content."));
        Assert.assertTrue(content.contains("This is after the constraint."));
    }

    @Test
    public void testName()
    {
        final DeadboltHandler deadboltHandler = new AbstractNoPreAuthDeadboltHandler()
        {
            @Override
            public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context)
            {
                return CompletableFuture.supplyAsync(() -> Optional.of(new AbstractDynamicResourceHandler()
                {
                    @Override
                    public CompletionStage<Boolean> isAllowed(final String name,
                                                              final String meta,
                                                              final DeadboltHandler deadboltHandler,
                                                              final Http.Context ctx)
                    {
                        return CompletableFuture.completedFuture("foo".equals(name));
                    }
                }));
            }
        };
        final Content html = be.objectify.deadbolt.java.views.html.dynamicTest.dynamicOrContent.render("foo",
                                                                                                       "bar",
                                                                                                       deadboltHandler);
        final String content = Helpers.contentAsString(html);
        Assert.assertTrue(content.contains("This is before the constraint."));
        Assert.assertTrue(content.contains("This is protected by the constraint."));
        Assert.assertFalse(content.contains("This is default content in case the constraint denies access to the protected content."));
        Assert.assertTrue(content.contains("This is after the constraint."));
    }

    @Test
    public void testMeta()
    {
        final DeadboltHandler deadboltHandler = new AbstractNoPreAuthDeadboltHandler()
        {
            @Override
            public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context)
            {
                return CompletableFuture.supplyAsync(() -> Optional.of(new AbstractDynamicResourceHandler()
                {
                    @Override
                    public CompletionStage<Boolean> isAllowed(final String name,
                                                              final String meta,
                                                              final DeadboltHandler deadboltHandler,
                                                              final Http.Context ctx)
                    {
                        return CompletableFuture.completedFuture("bar".equals(meta));
                    }
                }));
            }
        };
        final Content html = be.objectify.deadbolt.java.views.html.dynamicTest.dynamicOrContent.render("foo",
                                                                                                       "bar",
                                                                                                       deadboltHandler);
        final String content = Helpers.contentAsString(html);
        Assert.assertTrue(content.contains("This is before the constraint."));
        Assert.assertTrue(content.contains("This is protected by the constraint."));
        Assert.assertFalse(content.contains("This is default content in case the constraint denies access to the protected content."));
        Assert.assertTrue(content.contains("This is after the constraint."));
    }

    @Test
    public void testInvalid()
    {
        final DeadboltHandler deadboltHandler = new AbstractNoPreAuthDeadboltHandler()
        {
            @Override
            public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context)
            {
                return CompletableFuture.supplyAsync(() -> Optional.of(new AbstractDynamicResourceHandler()
                {
                    @Override
                    public CompletionStage<Boolean> isAllowed(final String name,
                                                              final String meta,
                                                              final DeadboltHandler deadboltHandler,
                                                              final Http.Context ctx)
                    {
                        return CompletableFuture.completedFuture(false);
                    }
                }));
            }
        };
        final Content html = be.objectify.deadbolt.java.views.html.dynamicTest.dynamicOrContent.render("foo",
                                                                                                       "bar",
                                                                                                       deadboltHandler);
        final String content = Helpers.contentAsString(html);
        Assert.assertTrue(content.contains("This is before the constraint."));
        Assert.assertFalse(content.contains("This is protected by the constraint."));
        Assert.assertTrue(content.contains("This is default content in case the constraint denies access to the protected content."));
        Assert.assertTrue(content.contains("This is after the constraint."));
    }

    public HandlerCache handlers()
    {
        // using new instances of handlers in the test
        return new DefaultHandlerCache(null,
                                       new HashMap<>());
    }
}
