import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import ass1package.ProcessTickets; // Imports variables

public class A1 
{
    public static void main(String[] args) throws IOException 
    {
        // Runs with java A1 input/datafile(1 or 2 or 3...).txt
        File file = new File(args[0]);
        processfunction(file);
        
    }

    // This function resets the data so that it does not interfere with the output
    private static void resetData(List<ProcessTickets> list) 
    {
        for (ProcessTickets process : list) 
        {
            process.resetData();
        }
    }

    // This is the main function that processes all the algorithms and outputs the results as specified
    private static void processfunction(File file) throws FileNotFoundException 
    {
        Scanner scan = new Scanner(file);
        int disp = 0;
        int readState = 0;
        String id = "";
        int arrive = 0;
        int execSize = 0;
        int tickets = 0;

        List<ProcessTickets> list = new ArrayList<>();
        List<Integer> randomList = new ArrayList<>();

        while (scan.hasNextLine()) // Scan each line for specified strings
        {
            String s = scan.nextLine(); // Scanner
            String[] ts = s.trim().split(" "); // trims whitespace
            if (ts[0].equals("BEGIN")) 
            {
                readState = 1;
            } 
            else if (ts[0].equals("BEGINRANDOM")) 
            {
                readState = 3;
            } 
            else if (readState == 1) 
            {
                if (ts[0].equals("DISP:")) 
                {
                    disp = Integer.parseInt(ts[1]);
                } 
                else if (ts[0].equals("END")) 
                {
                    readState = 2;
                }
            } 
            else if (readState == 2) 
            {
                if (ts[0].equals("ID:")) 
                {
                    id = (ts[1]);
                } 
                else if (ts[0].equals("Arrive:")) 
                {
                    arrive = Integer.parseInt(ts[1]);
                } 
                else if (ts[0].equals("ExecSize:")) 
                {
                    execSize = Integer.parseInt(ts[1]);
                } 
                else if (ts[0].equals("Tickets:")) 
                {
                    tickets = Integer.parseInt(ts[1]);
                } 
                else if (ts[0].equals("END")) 
                {
                    list.add(new ProcessTickets(id, arrive, execSize, tickets));
                }
            } 
            else if (readState == 3) 
            {
                if (ts[0].equals("ENDRANDOM")) 
                {
                    readState = 4;
                } 
                else if (ts[0].equals("")) 
                {
                    // Do Nothing
                } 
                else 
                {
                    randomList.add(Integer.parseInt(ts[0]));
                }
            }
        }
        // This is where the algorithms get processed
        List<Double> fcfsAverage =FCFS(disp, list);
        resetData(list);
        List<Double> srtAverage = SRT(disp, list);
        resetData(list);
        List<Double> fbvAverage = FBV(disp, list);
        //LTR(randomList); - Not finished
        algorithmSummary(fcfsAverage, srtAverage, fbvAverage);
        scan.close();
    }

    // First Come First Served algorithm - Also helps with output layout
    public static List<Double> FCFS(int disp, List<ProcessTickets> list) 
    {
        System.out.println("FCFS:");
        int time = 0;
        Double averageTurn = 0.00;
        Double averageWait = 0.00;
        for (ProcessTickets process : list) 
        {
            time += disp;
            if (time < process.getArrive()) 
            {
                time = process.getArrive();
            }
            process.setWait(time - process.getArrive());
            System.out.format("T%d: %s\n", time, process.getID());
            time += process.getExecSize();
            process.setTurn(time - process.getArrive());
        }

        System.out.println();
        System.out.println("Process  Turnaround Time  Waiting Time");
        Double z = 0.00;
        Double y = 0.00;

        Collections.sort(list, A1::compareByID);
        for (ProcessTickets process : list) 
        {
            System.out.format("%-8s %-16d %-14d\n", process.getID(), process.getTurn(), process.getWait());
            z += process.getTurn();
            y += process.getWait();
        }

        averageTurn = z/list.size();
        averageWait = y/list.size();
        System.out.println();
        List<Double> averageList = new ArrayList<>();
        averageList.add(averageTurn);
        averageList.add(averageWait);

        return averageList;
    }

    // Shortest Remaining Time algorithm - Also helps wiht output layout
    public static List<Double> SRT(int disp, List<ProcessTickets> list) 
    {
        System.out.println("SRT:");
        int time = 0;
        boolean done = false;
        List<ProcessTickets> notArrived = new ArrayList<>(list);
        List<ProcessTickets> queue = new ArrayList<>();
        Double averageTurn = 0.00;
        Double averageWait = 0.00;
        for (ProcessTickets process : list) 
        {
            if (process.getArrive() == 0) 
            {
                queue.add(process);
                notArrived.remove(process);
            }
        }
        Collections.sort(queue, A1::compareByCurrentTime);
        while (!done) 
        {
            List<ProcessTickets> queueCopy = new ArrayList<>(queue);
            for (ProcessTickets process : queueCopy) 
            {
                time += disp;
                List<ProcessTickets> qe = new ArrayList<>(notArrived);
                for (ProcessTickets p : qe) 
                {
                    if (time - disp <= p.getArrive() && p.getArrive() <= time) 
                    {
                        queue.add(p);
                        notArrived.remove(p);
                    }
                }
                Collections.sort(queue, A1::compareByCurrentTime);
                int currentTime = process.getCurrentTime();
                int processTime = 0;
                System.out.format("T%d: %s\n", time, process.getID());
                while (currentTime != 0) 
                {
                    currentTime -= 1;
                    process.setCurrentTime(currentTime);
                    time += 1;
                    processTime += 1;
                    qe = new ArrayList<>(notArrived);
                    for (ProcessTickets p : qe) 
                    {
                        if (p.getArrive() == time) 
                        {
                            queue.add(p);
                            notArrived.remove(p);
                            Collections.sort(queue, A1::compareByCurrentTime);
                        }
                    }
                    if (!queue.get(0).getID().equals(process.getID())) 
                    {
                        break;
                    }
                }
                if (currentTime == 0) 
                {
                    process.setCurrentTime(0);
                    process.setWait(time - processTime - process.getArrive() - process.getWait());
                    process.setTurn(time - process.getArrive());
                    queue.remove(process);
                } 
                else 
                {
                    process.setWait(processTime + process.getWait());
                    break;
                }
            }

            if (queue.isEmpty()) 
            {
                done = true;
            }
        }

        System.out.println();
        System.out.println("Process  Turnaround Time  Waiting Time");
        Double z = 0.00;
        Double y = 0.00;

        Collections.sort(list, A1::compareByID);
        for (ProcessTickets process : list) 
        {
            System.out.format("%-8s %-16d %-14d\n", process.getID(), process.getTurn(), process.getWait());
            z += process.getTurn();
            y += process.getWait();
        }

        averageTurn = z/list.size();
        averageWait = y/list.size();
        System.out.println();
        List<Double> averageList = new ArrayList<>();
        averageList.add(averageTurn);
        averageList.add(averageWait);

        return averageList;
    }

    // Multi-Level Feedback Variable Algorithm - Also helps wiht output layout
    public static List<Double> FBV(int disp, List<ProcessTickets> list) 
    {
        System.out.println("FBV:");
        int time = 0;
        boolean done = false;
        List<ProcessTickets> queue = new ArrayList<>(list);
        Double averageTurn = 0.00;
        Double averageWait = 0.00;

        while (!done) 
        {
            List<ProcessTickets> newQueue = new ArrayList<>();
            for (ProcessTickets process : queue) 
            {
                if (time < process.getArrive() - disp) 
                {
                    newQueue.add(process);
                    continue;
                }
                time += disp;
                int execSize = process.getCurrentTime();
                int power = process.getPower();
                if (execSize <= power) 
                {
                    process.setCurrentTime(0);
                    process.setWait(time + process.getWait() - process.getArrive());
                } 
                else 
                {
                    process.setCurrentTime(execSize - power);
                    execSize = power;
                    process.setWait(process.getWait() - power);
                    newQueue.add(process);
                }
                System.out.format("T%d: %s\n", time, process.getID());
                time += execSize;
                process.setTurn(time);
                process.setPower(power * 2);
            }
            queue = newQueue;
            if (queue.isEmpty()) 
            {
                done = true;
            }
            Collections.sort(queue, A1::compareByID);
        }

        System.out.println();
        System.out.println("Process  Turnaround Time  Waiting Time");
        Double z = 0.00;
        Double y = 0.00;

        Collections.sort(list, A1::compareByID);
        for (ProcessTickets process : list) 
        {
            System.out.format("%-8s %-16d %-14d\n", process.getID(), process.getTurn(), process.getWait());
            z += process.getTurn();
            y += process.getWait();
        }

        averageTurn = z/list.size();
        averageWait = y/list.size();
        System.out.println();
        List<Double> averageList = new ArrayList<>();
        averageList.add(averageTurn);
        averageList.add(averageWait);
        
        return averageList;
    }

    // Lottery Algorithm - Not finished
    /*public static List<Double> LTR(List<Integer> list) 
    {
        int counter = 0;
        Random rand = new Random();
        int winner = rand.nextInt(list.size());
        Double z = 0.0;
        Double y = 0.0;

        for (Integer number : list)
        {
            counter += number;

            if (counter > winner)
            {
                ;
            }
        }

        Collections.sort(list, A1::compareByID);
        for (ProcessTickets process : list) 
        {
            System.out.format("%-8s %-16d %-14d\n", process.getID(), process.getTurn(), process.getWait());
            z += process.getTurn();
            y += process.getWait();
        }

        System.out.println();
        List<Double> averageList = new ArrayList<>();
        averageList.add(averageTurn);
        averageList.add(averageWait);
        return;
    }*/

    public static void algorithmSummary(List<Double> fcfsAverage, List<Double> srtAverage, List<Double> fbvAverage)
    {
        System.out.println("Summary");
        System.out.println("Algorithm  Average Turnaround Time  Waiting Time");
        System.out.format("FCFS       %-24.2f %-10.2f \n", fcfsAverage.get(0), fcfsAverage.get(1));
        System.out.format("SRT        %-24.2f %-10.2f \n", srtAverage.get(0), srtAverage.get(1));
        System.out.format("FBV        %-24.2f %-10.2f \n", fbvAverage.get(0), fbvAverage.get(1));
        System.out.println("LTR        NA                       NA         ");
        System.out.println();
        //return;
    }

    private static int compareByExecSize(ProcessTickets p0, ProcessTickets p1) 
    {
        return p0.getExecSize() - p1.getExecSize();
    }

    private static int compareByCurrentTime(ProcessTickets p0, ProcessTickets p1) 
    {
        return p0.getCurrentTime() - p1.getCurrentTime();
    }

    private static int compareByID(ProcessTickets p0, ProcessTickets p1) 
    {
        return Integer.parseInt(p0.getID().substring(1)) - Integer.parseInt(p1.getID().substring(1));
    }
}