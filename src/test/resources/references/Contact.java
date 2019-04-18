public class Contact {
    private String name;
    public Integer age;
    protected Address address;
    public String getStreetName()
    {
        return address.getStreet();
    }
}
