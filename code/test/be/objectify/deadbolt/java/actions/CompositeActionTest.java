/*
 * Copyright 2010-2017 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.objectify.deadbolt.java.actions;

import be.objectify.deadbolt.java.ConstraintLogic;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.BeforeAuthCheckCache;
import be.objectify.deadbolt.java.cache.CompositeCache;
import be.objectify.deadbolt.java.cache.HandlerCache;
import be.objectify.deadbolt.java.composite.Constraint;
import com.typesafe.config.ConfigFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import play.mvc.Http;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class CompositeActionTest
{
    @Test
    public void testApplyRestriction() throws Exception
    {
        final Composite composite = Mockito.mock(Composite.class);
        Mockito.when(composite.value())
               .thenReturn("foo");
        Mockito.when(composite.meta())
               .thenReturn("bar");
        Mockito.when(composite.content())
               .thenReturn("x/y");
        final CompositeCache compositeCache = Mockito.mock(CompositeCache.class);

        final Constraint constraint = Mockito.mock(Constraint.class);
        Mockito.when(constraint.test(Mockito.any(Http.Request.class),
                                     Mockito.any(DeadboltHandler.class),
                                     Mockito.eq(Optional.of("bar")),
                                     Mockito.any(BiFunction.class)))
               .then(invocation -> ((Optional<String>)invocation.getArguments()[2]).map(meta -> meta.equals("bar"))
                                                                                   .map(CompletableFuture::completedFuture)
                                                                                   .orElse(CompletableFuture.completedFuture(false)));

        Mockito.when(compositeCache.apply("foo"))
               .thenReturn(Optional.of(constraint));

        final CompositeAction action = new CompositeAction(Mockito.mock(HandlerCache.class),
                                                           Mockito.mock(BeforeAuthCheckCache.class),
                                                           ConfigFactory.load(),
                                                           compositeCache,
                                                           Mockito.mock(ConstraintLogic.class));
        action.configuration = composite;

        final Http.Request request = Mockito.mock(Http.Request.class);
        final DeadboltHandler handler = Mockito.mock(DeadboltHandler.class);
        action.applyRestriction(request,
                                handler);

        Mockito.verify(constraint).test(Mockito.eq(request),
                                        Mockito.eq(handler),
                                        Mockito.eq(Optional.of("bar")),
                                        Mockito.any(BiFunction.class));
    }

    @Test
    public void testGetHandlerKey() throws Exception
    {
        final Composite composite = Mockito.mock(Composite.class);
        Mockito.when(composite.handlerKey())
               .thenReturn("foo");
        final CompositeAction action = new CompositeAction(Mockito.mock(HandlerCache.class),
                                                           Mockito.mock(BeforeAuthCheckCache.class),
                                                           ConfigFactory.load(),
                                                           Mockito.mock(CompositeCache.class),
                                                           Mockito.mock(ConstraintLogic.class));
        action.configuration = composite;

        Assert.assertEquals("foo",
                            action.getHandlerKey());
    }
}
