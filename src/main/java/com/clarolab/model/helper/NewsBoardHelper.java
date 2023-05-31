package com.clarolab.model.helper;

import com.clarolab.model.BuildTriage;
import com.clarolab.model.TestCase;
import com.clarolab.model.User;

public class NewsBoardHelper {
    public final static String[] congratulations = {"Awesome", "Congrats", "Way to go", "Hooray", "Great", "Impressive",
            "Go go go", "Eureka!", "Fantastic", "Extraordinary", "Wonderful", "Nice", "So Good", "Impressive", "Excellent",
            "Amazing", "Pleasant", "Marvelous", "Exceptional", "Super", "Outstanding", "Terrific", "Splendid",
            "Stupendous", "Astonishing", "May the Force be with you", "Today is your day", "Beautiful", "Applause", "Bravo",
            "Encore", "Three cheers", "Woo-Hoo, ", "Yippee", "Hurrah", "Huzzah", "Cheers", "Hats off to you", "Here’s to you",
            "Good show", "Keep believing", "Nice one", "Nice going", "Nicely done", "Thrilled", "Tackled", "High five", "I salute you",
            "Way to be", "Way to work", "Way to shine", "Well done", "You amaze me"};

    public static String passingEvent(TestCase test, User user) {
        String displayName = user == null ? "Unassigned" : user.getDisplayName();
        String fullName = test == null ? "Undefined" : test.getFullName();
        return String.format("%s made start passing %s. %s", displayName, fullName, getCongratulationsMessage());
    }

    public static String newAutomationEvent(TestCase test, User user) {
        if (user == null) {
            return String.format("%s requires a fix and is unassigned", test.getFullName());
        } else {
            return String.format("%s plans to fix %s. %s", user.getDisplayName(), test.getFullName(), getCongratulationsMessage());
        }
    }

    public static String fixedEvent(TestCase test, User user) {
        String displayName = user == null ? "Unassigned" : user.getDisplayName();
        String fullName = test == null ? "Undefined" : test.getFullName();
        return String.format("%s made %s solid. %s", displayName, fullName, getCongratulationsMessage());
    }

    public static String buildTriaged(BuildTriage buildTriage) {
        return String.format("Suite: %s #%d triaged. %s", buildTriage.getExecutorName(), buildTriage.getNumber(), getCongratulationsMessage());
    }

    public static String buildCreated(BuildTriage buildTriage, long fails) {
        return String.format("Suite Imported: %s #%d with fails: %d.", buildTriage.getExecutorName(), buildTriage.getNumber(), fails);
    }

    public static String newBuild(BuildTriage buildTriage) {
        return String.format("Suite: %s #%d just received.", buildTriage.getExecutorName(), buildTriage.getNumber());
    }

    public static String getCongratulationsMessage() {
        String congrat = congratulations[(int) (Math.random() * (congratulations.length - 1))];
        return congrat + "!";
    }

    public static String yesterdayTotalPending(long totalToTriage) {
        return String.format("Starting the day with %d tests pending to triage.", totalToTriage);
    }

    public static String yesterdayTriaged(String username) {
        return String.format("%s %s triaged more tests than anyone!", getCongratulationsMessage(), username);
    }

}
