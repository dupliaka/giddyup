package org.acme.schooltimetabling.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Pony {

    @PlanningId
    private Long id;

    private String name;

    @PlanningVariable
    private Timeslot timeslot;
    @PlanningVariable
    private Rider rider;

    // No-arg constructor required for OptaPlanner
    public Pony() {
    }

    public Pony(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Pony(long id, String name, Timeslot timeslot, Rider rider) {
        this(id, name);
        this.timeslot = timeslot;
        this.rider = rider;
    }

    @Override
    public String toString() {
        return name + "(" + id + ")";
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

}
