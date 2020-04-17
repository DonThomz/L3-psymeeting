package data;

import java.util.Calendar;

public class Job {

    // --------------------
    //   Attributes
    // --------------------

    private int job_id;
    private String job_name;
    private Calendar job_date;

    // --------------------
    //   Constructors
    // --------------------

    public Job(int job_id, String job_name, Calendar job_date) {
        this.job_id = job_id;
        this.job_name = job_name;
        this.job_date = job_date;
    }

    // --------------------
    //   Get methods
    // --------------------

    public int getJob_id() {
        return job_id;
    }

    public Calendar getJob_date() {
        return job_date;
    }

    public String getJob_name() {
        return job_name;
    }

    // --------------------
    //   Set methods
    // --------------------

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public void setJob_date(Calendar job_date) {
        this.job_date = job_date;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    // --------------------
    //   Statement methods
    // --------------------
}
