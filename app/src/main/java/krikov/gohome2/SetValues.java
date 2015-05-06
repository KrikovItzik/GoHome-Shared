package krikov.gohome2;

//Each row in the database can be represented by an object
//Columns will represent the objects properties
public class SetValues {

    private int _id;
    private String _teken;

    public SetValues() {
    }

    public SetValues(String teken) {
        this._teken = teken;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_teken(String _teken) {
        this._teken = _teken;
    }

    public int get_id() {
        return _id;
    }

    public String get_teken() {
        return _teken;
    }

}