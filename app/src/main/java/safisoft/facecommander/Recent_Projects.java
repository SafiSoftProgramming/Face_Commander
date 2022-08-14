package safisoft.facecommander;

public class Recent_Projects {

    private int id;
    private String Recent_Project_name;
    private String Recent_Project_Description;



    public Recent_Projects(int id, String Recent_Project_name, String Recent_Project_Description) {
        this.id = id;
        this.Recent_Project_name = Recent_Project_name;
        this.Recent_Project_Description = Recent_Project_Description;

    }

    public int Get_Id() {
        return id;
    }

    public String Get_Recent_Project_name() {
        return Recent_Project_name;
    }


    public String Get_Recent_Project_Description() {
        return Recent_Project_Description;
    }



}
