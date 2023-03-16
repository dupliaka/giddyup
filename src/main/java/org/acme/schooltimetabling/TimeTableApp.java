package org.acme.schooltimetabling;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.acme.schooltimetabling.domain.Pony;
import org.acme.schooltimetabling.domain.Rider;
import org.acme.schooltimetabling.domain.TimeTable;
import org.acme.schooltimetabling.domain.Timeslot;
import org.acme.schooltimetabling.solver.TimeTableConstraintProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeTableApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTableApp.class);

    public static void main(String[] args) {
        SolverFactory<TimeTable> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(TimeTable.class)
                .withEntityClasses(Pony.class)
                .withConstraintProviderClass(TimeTableConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(5)));

        // Load the problem
        TimeTable problem = generateDemoData();

        // Solve the problem
        Solver<TimeTable> solver = solverFactory.buildSolver();
        TimeTable solution = solver.solve(problem);

        // Visualize the solution
        printTimetable(solution);
    }

    public static TimeTable generateDemoData() {
        List<Timeslot> timeslotList = new ArrayList<>(10);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));


        List<Rider> riderList = new ArrayList<>(3);
        riderList.add(new Rider("B. Madden"));
        riderList.add(new Rider("G. Morris"));
        riderList.add(new Rider("I. Millar"));
        riderList.add(new Rider("L. Kraut"));

        List<Pony> ponyList = new ArrayList<>();
        long id = 0;
        ponyList.add(new Pony(id++, "R. Dash"));
        ponyList.add(new Pony(id++, "Applejack"));
        ponyList.add(new Pony(id++, "P. Pie"));
        ponyList.add(new Pony(id++, "Fluttershy"));
        ponyList.add(new Pony(id++, "Rarity"));
        ponyList.add(new Pony(id++, "Spike"));
        ponyList.add(new Pony(id++, "S. Glimmer"));
        ponyList.add(new Pony(id++, "T. Sparkle"));
        ponyList.add(new Pony(id++, "S. Shimmer"));
        ponyList.add(new Pony(id++, "Trixie"));
        ponyList.add(new Pony(id++, "Celestia"));
        ponyList.add(new Pony(id++, "Luna"));
        ponyList.add(new Pony(id++, "Discord"));
        ponyList.add(new Pony(id++, "BigMac"));
        ponyList.add(new Pony(id++, "G. Smith"));
        ponyList.add(new Pony(id++, "Zecora"));
        ponyList.add(new Pony(id++, "M. Pie"));
        ponyList.add(new Pony(id++, "A. Bloom"));
        ponyList.add(new Pony(id++, "S. Belle"));
        ponyList.add(new Pony(id++, "Scootaloo"));

        return new TimeTable(timeslotList, riderList, ponyList);
    }

    private static void printTimetable(TimeTable timeTable) {
        LOGGER.info("");
        List<Rider> riderList = timeTable.getRiderList();
        List<Pony> ponyList = timeTable.getPonyList();
        Map<Timeslot, Map<Rider, List<Pony>>> ponyMap = ponyList.stream()
                .filter(pony -> pony.getTimeslot() != null && pony.getRider() != null)
                .collect(Collectors.groupingBy(Pony::getTimeslot, Collectors.groupingBy(Pony::getRider)));
        LOGGER.info("|            | " + riderList.stream()
                .map(rider -> String.format("%-10s", rider.getName())).collect(Collectors.joining(" | ")) + " |");
        LOGGER.info("|" + "------------|".repeat(riderList.size() + 1));
        for (Timeslot timeslot : timeTable.getTimeslotList()) {
            List<List<Pony>> cellList = riderList.stream()
                    .map(rider -> {
                        Map<Rider, List<Pony>> byRiderMap = ponyMap.get(timeslot);
                        if (byRiderMap == null) {
                            return Collections.<Pony>emptyList();
                        }
                        List<Pony> cellPonyList = byRiderMap.get(rider);
                        if (cellPonyList == null) {
                            return Collections.<Pony>emptyList();
                        }
                        return cellPonyList;
                    })
                    .collect(Collectors.toList());

            LOGGER.info("| " + String.format("%-10s",
                    timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime()) + " | "
                    + cellList.stream().map(cellPonyList -> String.format("%-10s",
                            cellPonyList.stream().map(Pony::getName).collect(Collectors.joining(", "))))
                            .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|" + "------------|".repeat(riderList.size() + 1));
        }
        List<Pony> unassignedPonies = ponyList.stream()
                .filter(pony -> pony.getTimeslot() == null || pony.getRider() == null)
                .collect(Collectors.toList());
        if (!unassignedPonies.isEmpty()) {
            LOGGER.info("");
            LOGGER.info("Unassigned pony");
            for (Pony pony : unassignedPonies) {
                LOGGER.info("  " + pony.getName() );
            }
        }
    }

}
