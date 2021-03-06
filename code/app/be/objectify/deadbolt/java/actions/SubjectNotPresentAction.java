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
import be.objectify.deadbolt.java.ConstraintPoint;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.BeforeAuthCheckCache;
import be.objectify.deadbolt.java.cache.HandlerCache;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/**
 * Implements the {@link SubjectNotPresent} functionality, i.e. the
 * {@link be.objectify.deadbolt.java.models.Subject} provided by the {@link DeadboltHandler}
 * must be null to have access to the resource.
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
public class SubjectNotPresentAction extends AbstractSubjectAction<SubjectNotPresent>
{
    @Inject
    public SubjectNotPresentAction(final HandlerCache handlerCache,
                                   final BeforeAuthCheckCache beforeAuthCheckCache,
                                   final com.typesafe.config.Config config,
                                   final ConstraintLogic constraintLogic)
    {
        super(handlerCache,
              beforeAuthCheckCache,
              config,
              constraintLogic);
    }

    @Override
    CompletionStage<Result> present(final Http.RequestHeader request,
                                    final DeadboltHandler handler,
                                    final Optional<String> content)
    {
        return unauthorizeAndFail(request,
                                  handler,
                                  content);
    }

    @Override
    CompletionStage<Result> notPresent(final Http.RequestHeader request,
                                       final DeadboltHandler handler,
                                       final Optional<String> content)
    {
        return authorizeAndExecute(request);
    }

    @Override
    protected Supplier<CompletableFuture<Result>> testSubject(final ConstraintLogic constraintLogic,
                                                              final Http.RequestHeader request,
                                                              final DeadboltHandler deadboltHandler)
    {
        return () -> constraintLogic.subjectNotPresent(request,
                                                        deadboltHandler,
                                                        getContent(),
                                                        this::present,
                                                        this::notPresent,
                                                        ConstraintPoint.CONTROLLER).toCompletableFuture();
    }

    @Override
    protected boolean deferred() {
        return configuration.deferred();
    }

    @Override
    public Optional<String> getContent()
    {
        return Optional.ofNullable(configuration.content());
    }

    @Override
    public String getHandlerKey()
    {
        return configuration.handlerKey();
    }

    @Override
    public boolean isForceBeforeAuthCheck()
    {
        return configuration.forceBeforeAuthCheck();
    }
}
