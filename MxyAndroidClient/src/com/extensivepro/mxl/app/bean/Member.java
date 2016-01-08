package com.extensivepro.mxl.app.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

public class Member extends BaseObject
{
    
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 1L;

    @Expose
    private double deposit;

    @Expose
    private int score;

    @Expose
    private String id;

    @Expose
    private String username;

    @Expose
    private boolean isAccountLocked;

    @Expose
    private String zipCode;

    @Expose
    private String name;

    @Expose
    private String gender;

    @Expose
    private boolean isAccountEnabled;

    @Expose
    private String phone;

    @Expose
    private String address;

    @Expose
    private String email;

    @Expose
    private String areaStore;

    @Expose
    private String password;
    
    private List<Receiver> receivers;
    
    private Receiver defaultReceiver;
    
    private boolean hasGotFreeSale;
    
    public double getDeposit()
    {
        return deposit;
    }

    public void setDeposit(double deposit)
    {
        this.deposit = deposit;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public boolean isAccountLocked()
    {
        return isAccountLocked;
    }

    public void setAccountLocked(boolean isAccountLocked)
    {
        this.isAccountLocked = isAccountLocked;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public boolean isAccountEnabled()
    {
        return isAccountEnabled;
    }

    public void setAccountEnabled(boolean isAccountEnabled)
    {
        this.isAccountEnabled = isAccountEnabled;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getAreaStore()
    {
        return areaStore;
    }

    public void setAreaStore(String areaStore)
    {
        this.areaStore = areaStore;
    }

    @Override
    public String toString()
    {
        return "Member [deposit=" + deposit + ", score=" + score + ", id=" + id
                + ", username=" + username + ", isAccountLocked="
                + isAccountLocked + ", zipCode=" + zipCode + ", name=" + name
                + ", gender=" + gender + ", isAccountEnabled="
                + isAccountEnabled + ", phone=" + phone + ", address="
                + address + ", email=" + email + ", areaStore=" + areaStore
                + "]";
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void update(Member member)
    {
        if(member == null)
        {
            return;
        }
        this.deposit = member.getDeposit();
        this.score = member.getScore();
        this.id = member.getId();
        this.username = member.getUsername();
        this.isAccountLocked = member.isAccountLocked();
        this.zipCode = member.getZipCode();
        this.name = member.name;
        this.gender = member.gender;
        this.isAccountEnabled = member.isAccountEnabled();
        this.phone = member.getPhone();
        this.address = member.getAddress();
        this.email = member.getEmail();
        this.areaStore = member.getAreaStore();
    }

    public List<Receiver> getReceivers()
    {
        return receivers;
    }

    public void setReceivers(List<Receiver> receivers)
    {
        this.receivers = receivers;
    }

    public Receiver getDefaultReceiver()
    {
        setDefaultReceiver(null);
        if(receivers==null){
            return null;
        }
        for(Receiver receiver:receivers){
            if(receiver.isDefault()){
                defaultReceiver = receiver;
            }
        }
        return defaultReceiver;
    }

    public void setDefaultReceiver(Receiver defaultReceiver)
    {
        this.defaultReceiver = defaultReceiver;
    }

    public boolean isHasGotFreeSale()
    {
        return hasGotFreeSale;
    }

    public void setHasGotFreeSale(boolean hasGotFreeSale)
    {
        this.hasGotFreeSale = hasGotFreeSale;
    }
    
    
    
    
}
