package it.polimi.ingsw.communication.message.payload;

public class ReduceDemandChoice {

    private String choice;

    public ReduceDemandChoice() {}

    public ReduceDemandChoice(String choice) {
        this.choice = choice;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
