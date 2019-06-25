import javafx.beans.property.SimpleStringProperty;

public class Project {

    private SimpleStringProperty projectName = new SimpleStringProperty("");
    private SimpleStringProperty projectDate = new SimpleStringProperty("");
    private SimpleStringProperty projectPath = new SimpleStringProperty("");

    public Project() {
    }

    public Project(String projecName, String projectDate, String projectPath) {
        this.projectName = new  SimpleStringProperty(projecName);
        this.projectDate = new SimpleStringProperty(projectDate);
        this.projectPath = new SimpleStringProperty(projectPath);
    }

    public String getProjectName() {return projectName.get();}
    public void setProjectName(String projecName) {this.projectName.set(projecName);}
    public SimpleStringProperty projectNameProperty(){
        return projectName;
    }

    public String getProjectDate() {return projectDate.get();}
    public void setProjectDate(String projectDate) {this.projectDate.set(projectDate);}
    public SimpleStringProperty projectDateProperty(){
        return projectDate;
    }

    public String getProjectPath() {
        return projectPath.get();
    }
    public void setProjectPath(String projectPath) {
        this.projectPath.set(projectPath);
    }
    public SimpleStringProperty projectPathProperty() {
        return projectPath;
    }

    @Override
    public String toString() {
        return getProjectName() + "|" + getProjectDate() + "|" + getProjectPath();
    }
}
