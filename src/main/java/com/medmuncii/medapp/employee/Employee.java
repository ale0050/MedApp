package com.medmuncii.medapp.employee;

import jakarta.persistence.*;
import lombok.*;
import com.medmuncii.medapp.company.Company;

@Entity
@Data
@AllArgsConstructor
@Table(name="employes")
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;

    @Column(unique = true)
    private String cnp;

    private String position;
    private String workplace;
    private String email;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Company company;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getWorkplace() { return workplace; }
    public void setWorkplace(String workplace) { this.workplace = workplace; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
}
