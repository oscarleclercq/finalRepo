package ie.ucd.smartride;

public class CoopCompeteV2Data {

    private int _id;
    private float _recentHumanPowerAvg;
    private float _recentMotorPowerAvg;
    private float _motorOutputPowerReference;
    private float _pollutionLevel;
    private float _requestToMotor;
    private float _mTarget;
    private float _mActual;
    private float _mainSamplindPeriod;


    public CoopCompeteV2Data(float humanPowerAvg, float recentMotorPowerAvg, float motorOutputPowerReference, float pollutionLevel, float requestToMotor, float mTarget, float mActual, float mainSamplingPeriod){
        this._recentHumanPowerAvg=humanPowerAvg;
        this._recentMotorPowerAvg=recentMotorPowerAvg;
        this._motorOutputPowerReference=motorOutputPowerReference;
        this._pollutionLevel=pollutionLevel;
        this._requestToMotor=requestToMotor;
        this._mTarget=mTarget;
        this._mActual=mActual;
        this._mainSamplindPeriod = mainSamplingPeriod;
    }


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public float get_recentHumanPowerAvg() {
        return _recentHumanPowerAvg;
    }

    public void set_recentHumanPowerAvg(float _recentHumanPowerAvg) {
        this._recentHumanPowerAvg = _recentHumanPowerAvg;
    }

    public float get_recentMotorPowerAvg() {
        return _recentMotorPowerAvg;
    }

    public void set_recentMotorPowerAvg(float _recentMotorPowerAvg) {
        this._recentMotorPowerAvg = _recentMotorPowerAvg;
    }

    public float get_motorOutputPowerReference() {
        return _motorOutputPowerReference;
    }

    public void set_motorOutputPowerReference(float _motorOutputPowerReference) {
        this._motorOutputPowerReference = _motorOutputPowerReference;
    }

    public float get_pollutionLevel() {
        return _pollutionLevel;
    }

    public void set_pollutionLevel(float _pollutionLevel) {
        this._pollutionLevel = _pollutionLevel;
    }

    public float get_requestToMotor() {
        return _requestToMotor;
    }

    public void set_requestToMotor(float _requestToMotor) {
        this._requestToMotor = _requestToMotor;
    }

    public float get_mTarget() {
        return _mTarget;
    }

    public void set_mTarget(float _mTarget) {
        this._mTarget = _mTarget;
    }

    public float get_mActual() {
        return _mActual;
    }

    public void set_mActual(float _mActual) {
        this._mActual = _mActual;
    }

    public float get_mainSamplindPeriod() {
        return _mainSamplindPeriod;
    }

    public void set_mainSamplindPeriod(float _mainSamplindPeriod) {
        this._mainSamplindPeriod = _mainSamplindPeriod;
    }
}
