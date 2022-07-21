package ass1package;

// Tracks variables with getters and setters
public class ProcessTickets 
{
    String id;
    int arrive;
    int execSize;
    int tickets;
    int wait = 0;
    int turn = 0;
    int currentTime;
    int power = 1;

    public ProcessTickets(String id, int arrive, int execSize, int tickets) 
    {
        this.id = id;
        this.arrive = arrive;
        this.execSize = execSize;
        this.tickets = tickets;
        this.currentTime = execSize;
    }

    public void resetData() 
    {
        this.currentTime = execSize;
        this.wait = 0;
        this.turn = 0;
        this.power = 1;
    }

    public int getPower() 
    {
        return power;
    }

    public void setPower(int power) 
    {
        this.power = power;
    }

    public int getCurrentTime() 
    {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) 
    {
        this.currentTime = currentTime;
    }

    public String getID() 
    {
        return id;
    }

    public int getArrive() 
    {
        return arrive;
    }

    public int getExecSize() 
    {
        return execSize;
    }

    public int getTickets() 
    {
        return tickets;
    }

    public int getWait() 
    {
        return wait;
    }

    public void setWait(int wait) 
    {
        this.wait = wait;
    }

    public int getTurn() 
    {
        return turn;
    }

    public void setTurn(int turn) 
    {
        this.turn = turn;
    }
}