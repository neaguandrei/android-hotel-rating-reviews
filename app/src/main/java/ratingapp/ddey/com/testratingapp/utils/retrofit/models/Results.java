package ratingapp.ddey.com.testratingapp.utils.retrofit.models;

public class Results {
    private String icon;

    private String place_id;

    private String scope;

    private String reference;

    private Geometry geometry;

    private OpeningHours openingHours;

    private String id;

    private Photos[] photos;

    private String vicinity;

    private String name;

    private PlusCode plus_code;

    private String rating;

    private String[] types;

    public String getIcon ()
    {
        return icon;
    }

    public void setIcon (String icon)
    {
        this.icon = icon;
    }

    public String getPlace_id ()
    {
        return place_id;
    }

    public void setPlace_id (String place_id)
    {
        this.place_id = place_id;
    }

    public String getScope ()
    {
        return scope;
    }

    public void setScope (String scope)
    {
        this.scope = scope;
    }

    public String getReference ()
    {
        return reference;
    }

    public void setReference (String reference)
    {
        this.reference = reference;
    }

    public Geometry getGeometry ()
    {
        return geometry;
    }

    public void setGeometry (Geometry geometry)
    {
        this.geometry = geometry;
    }

    public OpeningHours getOpeningHours()
    {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours)
    {
        this.openingHours = openingHours;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Photos[] getPhotos ()
    {
        return photos;
    }

    public void setPhotos (Photos[] photos)
    {
        this.photos = photos;
    }

    public String getVicinity ()
    {
        return vicinity;
    }

    public void setVicinity (String vicinity)
    {
        this.vicinity = vicinity;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public PlusCode getPlus_code ()
    {
        return plus_code;
    }

    public void setPlus_code (PlusCode plus_code)
    {
        this.plus_code = plus_code;
    }

    public String getRating ()
    {
        return rating;
    }

    public void setRating (String rating)
    {
        this.rating = rating;
    }

    public String[] getTypes ()
    {
        return types;
    }

    public void setTypes (String[] types)
    {
        this.types = types;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [icon = "+icon+", place_id = "+place_id+", scope = "+scope+", reference = "+reference+", geometry = "+geometry+", openingHours = "+ openingHours +", id = "+id+", photos = "+photos+", vicinity = "+vicinity+", name = "+name+", plus_code = "+plus_code+", rating = "+rating+", types = "+types+"]";
    }
}
