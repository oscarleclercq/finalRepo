package ie.ucd.smartride;

public class MotorPowerControlData {
    private int _id;
    private String _commandSent;
    private float _gainParameter;
    private float _humanPowerAvg;
    private float _motorPowerAvg;
    private float _motorPowerTarget;
    private float _motorPowerError;
    private float _samplingPeriod;

    public MotorPowerControlData(String requestToSendToBike, float gainParameter, float humanPowerAvg, float motorPowerAvg,
                                float motorPowerTarget, float motorPowerError, float samplingPeriod){
        this._commandSent = requestToSendToBike;
        this._gainParameter = gainParameter;
        this._humanPowerAvg = humanPowerAvg;
        this._motorPowerAvg = motorPowerAvg;
        this._motorPowerTarget = motorPowerTarget;
        this._motorPowerError = motorPowerError;
        this._samplingPeriod = samplingPeriod;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_commandSent() {
        return _commandSent;
    }

    public void set_commandSent(String _commandSent) {
        this._commandSent = _commandSent;
    }

    public float get_gainParameter() {
        return _gainParameter;
    }

    public void set_gainParameter(float _gainParameter) {
        this._gainParameter = _gainParameter;
    }

    public float get_humanPowerAvg() {
        return _humanPowerAvg;
    }

    public void set_humanPowerAvg(float _humanPowerAvg) {
        this._humanPowerAvg = _humanPowerAvg;
    }

    public float get_motorPowerAvg() {
        return _motorPowerAvg;
    }

    public void set_motorPowerAvg(float _motorPowerAvg) {
        this._motorPowerAvg = _motorPowerAvg;
    }


    public float get_samplingPeriod() {
        return _samplingPeriod;
    }

    public void set_samplingPeriod(float _samplingPeriod) {
        this._samplingPeriod = _samplingPeriod;
    }

    public float get_motorPowerTarget() {
        return _motorPowerTarget;
    }

    public void set_motorPowerTarget(float _motorPowerTarget) {
        this._motorPowerTarget = _motorPowerTarget;
    }

    public float get_motorPowerError() {
        return _motorPowerError;
    }

    public void set_motorPowerError(float _motorPowerError) {
        this._motorPowerError = _motorPowerError;
    }

}
