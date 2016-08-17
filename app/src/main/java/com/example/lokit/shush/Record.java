package com.example.lokit.shush;

        import android.os.Parcel;
        import android.os.Parcelable;

        import java.util.ArrayList;
        import java.util.UUID;

public class Record implements Parcelable {

    // Note: An entity's partition and row key uniquely identify the entity in
    // the table.
    // Entities with the same partition key can be queried faster than those
    // with different partition keys.



    public String event_name;
    public String event_type;
    public String start_time;
    public String end_time;
    public int[] weekdays = new int[7];
    public Integer mute;
    public Integer vibrate;
    public double[] location = new double[3];

    public Record(Parcel in){
        this.event_name = in.readString();
        this.event_type = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.weekdays = in.createIntArray();
        this.mute = in.readInt();
        this.vibrate = in.readInt();
        this.location = in.createDoubleArray();
    }

    public Record() {

    }

    public void setename(String event_name) {
        this.event_name = event_name ;
    }

    public void setetype(String event_type) {
        this.event_type = event_type;
    }
    public String getstime() {
        return this.start_time;
    }

    public String getename() {
        return this.event_name;
    }
    public String getetype() {
        return this.event_type;
    }

    public void setstime(String email) {
        this.start_time = email;
    }

    public String getetime() {
        return this.end_time;
    }

    public void setetime(String email) {
        this.end_time = email;
    }

    public Integer getmute() {
        return this.mute;
    }

    public void setmute(Integer email) {
        this.mute = email;
    }

    public Integer getvibr() {
        return this.vibrate;
    }

    public void setvibr(Integer email) {
        this.vibrate = email;
    }

    public double[] getLocation(){
        return this.location;
    }

    public void setLocation(ArrayList<Double> email){
        this.location = new double[3];
        location[0]=(email.get(0));
        location[1]=(email.get(1));
        location[2]=(email.get(2));

    }

    public int[] getWeekdays(){
        return this.weekdays;
    }

    public void setWeekdays(ArrayList<Integer> email){
        this.weekdays = new int[7];
        weekdays[0] = email.get(0);
        weekdays[1] = email.get(1);
        weekdays[2] = email.get(2);
        weekdays[3] = email.get(3);
        weekdays[4] = email.get(4);
        weekdays[5] = email.get(5);
        weekdays[6] = email.get(6);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(event_name);
        dest.writeString(event_type);
        dest.writeString(start_time);
        dest.writeString(end_time);
        dest.writeIntArray(weekdays);
        dest.writeInt(mute);
        dest.writeInt(vibrate);
        dest.writeDoubleArray(location);

    }

    public static final Parcelable.Creator<Record> CREATOR = new Parcelable.Creator<Record>() {

        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}

