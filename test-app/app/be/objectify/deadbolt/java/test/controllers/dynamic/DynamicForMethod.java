package be.objectify.deadbolt.java.test.controllers.dynamic;

import be.objectify.deadbolt.java.actions.Dynamic;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class DynamicForMethod extends Controller
{
    @Dynamic("niceName")
    public CompletionStage<Result> userMustHaveTheSameNameAsMyWife()
    {
        return CompletableFuture.supplyAsync(() -> ok("Content accessible"));
    }
}
