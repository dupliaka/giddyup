package org.acme.schooltimetabling.solver;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.acme.schooltimetabling.domain.Pony;
import org.acme.schooltimetabling.domain.Rider;
import org.acme.schooltimetabling.domain.TimeTable;
import org.acme.schooltimetabling.domain.Timeslot;
import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class TimeTableConstraintProviderTest {

    private static final Rider RIDER_1 = new Rider("Rider1");
    private static final Rider RIDER_2 = new Rider("Rider2");
    private static final Timeslot TIMESLOT1 = new Timeslot(DayOfWeek.MONDAY, LocalTime.NOON);
    private static final Timeslot TIMESLOT2 = new Timeslot(DayOfWeek.TUESDAY, LocalTime.NOON);
    private static final Timeslot TIMESLOT3 = new Timeslot(DayOfWeek.TUESDAY, LocalTime.NOON.plusHours(1));
    private static final Timeslot TIMESLOT4 = new Timeslot(DayOfWeek.TUESDAY, LocalTime.NOON.plusHours(3));

    ConstraintVerifier<TimeTableConstraintProvider, TimeTable> constraintVerifier = ConstraintVerifier.build(
            new TimeTableConstraintProvider(), TimeTable.class, Pony.class);

    @Test
    void riderConflict() {
        Pony firstPony = new Pony(1, "Pony1", TIMESLOT1, RIDER_1);
        Pony conflictingPony = new Pony(2, "Pony2", TIMESLOT1, RIDER_1);
        Pony nonConflictingPony = new Pony(3, "Pony3", TIMESLOT2, RIDER_1);
        constraintVerifier.verifyThat(TimeTableConstraintProvider::riderConflict)
                .given(firstPony, conflictingPony, nonConflictingPony)
                .penalizesBy(1);
    }
}
