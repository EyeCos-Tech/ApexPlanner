package Responses;

/**
 *
 * @author Pau Savall
 */
public class UserResponse extends Response {
    
    String ages;

    public UserResponse(String ages, String name) {
        this.ages = ages;
        super.name= name;
    }

    public String getAges() {
        return ages;
    }

    public void setAges(String ages) {
        this.ages = ages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
    
    
}
