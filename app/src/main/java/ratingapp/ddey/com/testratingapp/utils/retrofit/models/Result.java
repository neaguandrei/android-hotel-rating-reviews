package ratingapp.ddey.com.testratingapp.utils.retrofit.models;

import ratingapp.ddey.com.testratingapp.models.GoogleReview;

public class Result {
    private String icon;

    private String place_id;

    private GoogleReview[] reviews;

    private String scope;

    private String website;

    private String international_phone_number;

    private String adr_address;

    private String url;

    private String reference;

    private Geometry geometry;

    private String utc_offset;

    private String id;

    private String vicinity;

    private AddressComponents[] address_components;

    private String name;

    private String formatted_address;

    private String formatted_phone_number;

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

    public GoogleReview[] getReviews ()
    {
        return reviews;
    }

    public void setReviews (GoogleReview[] reviews)
    {
        this.reviews = reviews;
    }

    public String getScope ()
    {
        return scope;
    }

    public void setScope (String scope)
    {
        this.scope = scope;
    }

    public String getWebsite ()
    {
        return website;
    }

    public void setWebsite (String website)
    {
        this.website = website;
    }

    public String getInternational_phone_number ()
    {
        return international_phone_number;
    }

    public void setInternational_phone_number (String international_phone_number)
    {
        this.international_phone_number = international_phone_number;
    }

    public String getAdr_address ()
    {
        return adr_address;
    }

    public void setAdr_address (String adr_address)
    {
        this.adr_address = adr_address;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
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

    public String getUtc_offset ()
    {
        return utc_offset;
    }

    public void setUtc_offset (String utc_offset)
    {
        this.utc_offset = utc_offset;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getVicinity ()
    {
        return vicinity;
    }

    public void setVicinity (String vicinity)
    {
        this.vicinity = vicinity;
    }

    public AddressComponents[] getAddress_components ()
    {
        return address_components;
    }

    public void setAddress_components (AddressComponents[] address_components)
    {
        this.address_components = address_components;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getFormatted_address ()
    {
        return formatted_address;
    }

    public void setFormatted_address (String formatted_address)
    {
        this.formatted_address = formatted_address;
    }

    public String getFormatted_phone_number ()
    {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number (String formatted_phone_number)
    {
        this.formatted_phone_number = formatted_phone_number;
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
        return "ClassPojo [icon = "+icon+", place_id = "+place_id+", reviews = "+reviews+", scope = "+scope+", website = "+website+", international_phone_number = "+international_phone_number+", adr_address = "+adr_address+", url = "+url+", reference = "+reference+", geometry = "+geometry+", utc_offset = "+utc_offset+", id = "+id+", vicinity = "+vicinity+", address_components = "+address_components+", name = "+name+", formatted_address = "+formatted_address+", formatted_phone_number = "+formatted_phone_number+", rating = "+rating+", types = "+types+"]";
    }
}
