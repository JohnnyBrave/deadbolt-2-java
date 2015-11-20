package be.objectify.deadbolt.java.test.controllers.subject;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class SubjectPresentForMethod extends Controller
{
    @SubjectPresent
    public CompletionStage<Result> subjectMustBePresent()
    {
        return CompletableFuture.supplyAsync(() -> ok("Content accessible"));
    }
}
