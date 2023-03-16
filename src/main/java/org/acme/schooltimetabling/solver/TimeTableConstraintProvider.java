package org.acme.schooltimetabling.solver;

import java.time.Duration;

import org.acme.schooltimetabling.domain.Pony;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                riderConflict(constraintFactory),
                // Soft constraints
                ponyRecharge(constraintFactory),
        };
    }

    Constraint riderConflict(ConstraintFactory constraintFactory) {
        // A rider can occupy at most one pony at the same time.
        return constraintFactory
                // Select each pair of 2 different pony ...
                .forEachUniquePair(Pony.class,
                        // ... in the same timeslot ...
                        Joiners.equal(Pony::getTimeslot),
                        // ... with the same rider ...
                        Joiners.equal(Pony::getRider))
                // ... and penalize each pair with a hard weight.
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Rider conflict");
    }

    Constraint ponyRecharge(ConstraintFactory constraintFactory) {
        // There should be a break for horses to rest.
        return constraintFactory
                .forEach(Pony.class)
                .join(Pony.class, Joiners.equal(Pony::getId),
                        Joiners.equal((pony) -> pony.getTimeslot().getDayOfWeek()))
                .filter((pony1, pony2) -> {
                    Duration between = Duration.between(pony1.getTimeslot().getEndTime(),
                            pony2.getTimeslot().getStartTime());
                    return between.isZero();
                })
                .reward(HardSoftScore.ONE_HARD)
                .asConstraint("Pony recharge conflict");
    }
}
