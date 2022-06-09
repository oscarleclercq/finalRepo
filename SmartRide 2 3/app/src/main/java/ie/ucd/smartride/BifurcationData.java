package ie.ucd.smartride;

public class BifurcationData {
    private float _retardingFactor;
    private float _pollutionLevel;
    private float _cooperationThreshold;
    private float _recentHumanPower;
    private float _recentMotorPower;
    private float _f1;
    private float _f2;
    private float _f3;
    private float _f4;
    private float _nextOutputReferencePower;
    private float _f1_characteristicvalue;
    private float _f1_firstterm;
    private float _f1_secondterm;
    private float _f2_characteristicvalue;
    private float _f2_firstterm;
    private float _f2_secondterm;
    private float _f3_characteristicvalue;
    private float _f3_firstterm;
    private float _f3_secondterm;
    private float _f4_characteristicvalue;
    private float _f4_firstterm;
    private float _f4_secondterm;


    public BifurcationData(float retardingFactor,
                           float pollutionLevel,
                           float cooperationThreshold,
                           float recentHumanPower,
                           float recentMotorPower,
                           float f1,
                           float f2,
                           float f3,
                           float f4,
                           float nextOutputReferencePower,
                           float f1_characteristicvalue,
                           float f1_firstterm,
                           float f1_secondterm,
                           float f2_characteristicvalue,
                           float f2_firstterm,
                           float f2_secondterm,
                           float f3_characteristicvalue,
                           float f3_firstterm,
                           float f3_secondterm,
                           float f4_characteristicvalue,
                           float f4_firstterm,
                           float f4_secondterm){

        this._retardingFactor=retardingFactor;
        this._pollutionLevel=pollutionLevel;
        this._cooperationThreshold=cooperationThreshold;
        this._recentHumanPower=recentHumanPower;
        this._recentMotorPower=recentMotorPower;
        this._f1=f1;
        this._f2=f2;
        this._f3=f3;
        this._f4=f4;
        this._nextOutputReferencePower=nextOutputReferencePower;
        this._f1_characteristicvalue=f1_characteristicvalue;
        this._f1_firstterm=f1_firstterm;
        this._f1_secondterm=f1_secondterm;
        this._f2_characteristicvalue=f2_characteristicvalue;
        this._f2_firstterm=f2_firstterm;
        this._f2_secondterm=f2_secondterm;
        this._f3_characteristicvalue=f3_characteristicvalue;
        this._f3_firstterm=f3_firstterm;
        this._f3_secondterm=f3_secondterm;
        this._f4_characteristicvalue=f4_characteristicvalue;
        this._f4_firstterm=f4_firstterm;
        this._f4_secondterm=f4_secondterm;

    }


    public float get_retardingFactor() {
        return _retardingFactor;
    }

    public void set_retardingFactor(float _retardingFactor) {
        this._retardingFactor = _retardingFactor;
    }

    public float get_pollutionLevel() {
        return _pollutionLevel;
    }

    public void set_pollutionLevel(float _pollutionLevel) {
        this._pollutionLevel = _pollutionLevel;
    }

    public float get_cooperationThreshold() {
        return _cooperationThreshold;
    }

    public void set_cooperationThreshold(float _cooperationThreshold) {
        this._cooperationThreshold = _cooperationThreshold;
    }

    public float get_recentHumanPower() {
        return _recentHumanPower;
    }

    public void set_recentHumanPower(float _recentHumanPower) {
        this._recentHumanPower = _recentHumanPower;
    }

    public float get_recentMotorPower() {
        return _recentMotorPower;
    }

    public void set_recentMotorPower(float _recentMotorPower) {
        this._recentMotorPower = _recentMotorPower;
    }

    public float get_f1() {
        return _f1;
    }

    public void set_f1(float _f1) {
        this._f1 = _f1;
    }

    public float get_f2() {
        return _f2;
    }

    public void set_f2(float _f2) {
        this._f2 = _f2;
    }

    public float get_f3() {
        return _f3;
    }

    public void set_f3(float _f3) {
        this._f3 = _f3;
    }

    public float get_f4() {
        return _f4;
    }

    public void set_f4(float _f4) {
        this._f4 = _f4;
    }

    public float get_nextOutputReferencePower() {
        return _nextOutputReferencePower;
    }

    public void set_nextOutputReferencePower(float _nextOutputReferencePower) {
        this._nextOutputReferencePower = _nextOutputReferencePower;
    }

    public float get_f1_characteristicvalue() {
        return _f1_characteristicvalue;
    }

    public void set_f1_characteristicvalue(float _f1_characteristicvalue) {
        this._f1_characteristicvalue = _f1_characteristicvalue;
    }

    public float get_f1_firstterm() {
        return _f1_firstterm;
    }

    public void set_f1_firstterm(float _f1_firstterm) {
        this._f1_firstterm = _f1_firstterm;
    }

    public float get_f1_secondterm() {
        return _f1_secondterm;
    }

    public void set_f1_secondterm(float _f1_secondterm) {
        this._f1_secondterm = _f1_secondterm;
    }

    public float get_f2_characteristicvalue() {
        return _f2_characteristicvalue;
    }

    public void set_f2_characteristicvalue(float _f2_characteristicvalue) {
        this._f2_characteristicvalue = _f2_characteristicvalue;
    }

    public float get_f2_firstterm() {
        return _f2_firstterm;
    }

    public void set_f2_firstterm(float _f2_firstterm) {
        this._f2_firstterm = _f2_firstterm;
    }

    public float get_f2_secondterm() {
        return _f2_secondterm;
    }

    public void set_f2_secondterm(float _f2_secondterm) {
        this._f2_secondterm = _f2_secondterm;
    }

    public float get_f3_characteristicvalue() {
        return _f3_characteristicvalue;
    }

    public void set_f3_characteristicvalue(float _f3_characteristicvalue) {
        this._f3_characteristicvalue = _f3_characteristicvalue;
    }

    public float get_f3_firstterm() {
        return _f3_firstterm;
    }

    public void set_f3_firstterm(float _f3_firstterm) {
        this._f3_firstterm = _f3_firstterm;
    }

    public float get_f3_secondterm() {
        return _f3_secondterm;
    }

    public void set_f3_secondterm(float _f3_secondterm) {
        this._f3_secondterm = _f3_secondterm;
    }

    public float get_f4_characteristicvalue() {
        return _f4_characteristicvalue;
    }

    public void set_f4_characteristicvalue(float _f4_characteristicvalue) {
        this._f4_characteristicvalue = _f4_characteristicvalue;
    }

    public float get_f4_firstterm() {
        return _f4_firstterm;
    }

    public void set_f4_firstterm(float _f4_firstterm) {
        this._f4_firstterm = _f4_firstterm;
    }

    public float get_f4_secondterm() {
        return _f4_secondterm;
    }

    public void set_f4_secondterm(float _f4_secondterm) {
        this._f4_secondterm = _f4_secondterm;
    }
}
