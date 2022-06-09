package ie.ucd.smartride;

public class DataExchange {
    private int _id;
    private float _lastRequestToBike;
    private float _recentHumanPower;
    private float _recentMotorPower;
    private float _motorPowerTarget;
    private float _previousMotorPowerTarget;


    //multiple different types of constructor depending on the data

    // for initial creation
    public DataExchange(){

    }

    //bike data
//    public DataExchange(String commandSent){
//        this._commandSent = commandSent;
//    }


    //recent control values
//    public DataExchange(String commandSent){
//        this._commandSent = commandSent;
//    }


    // what is the data that needs to be exchanged?
    // consult retrieve methods





    public float getLastRequestToBike() {
        return _lastRequestToBike;
    }

    public void setLastRequestToBike(float _commandSent) {
        this._lastRequestToBike = _commandSent;
    }


    public float getRecentHumanPower() {
        return _recentHumanPower;
    }

    public void setRecentHumanPower(float recentHumanPower) {
        this._recentHumanPower = recentHumanPower;
    }

    public float getRecentMotorPower() {
        return _recentMotorPower;
    }

    public void setRecentMotorPower(float recentMotorPower) {
        this._recentMotorPower = recentMotorPower;
    }

    public float getMotorPowerTarget() {
        return _motorPowerTarget;
    }

    public void setMotorPowerTarget(float _motorPowerTarget) {
        this._motorPowerTarget = _motorPowerTarget;
    }

    public float getPreviousMotorPowerTarget() {
        return _previousMotorPowerTarget;
    }

    public void setPreviousMotorPowerTarget(float _previousMotorPowerTarget) {
        this._previousMotorPowerTarget = _previousMotorPowerTarget;
    }
}





//On the server
//def initialize():
//        self.gui = new GUI(self)
//
//def receive():
//




//On the GUI:
//
//def initialize(server):
//        self.server = server
//
//def receive_user_input(value):
//        self.server.receive(value)
