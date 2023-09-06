package pillihuaman.com.basebd.user;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pillihuaman.com.basebd.system.System;
import pillihuaman.com.basebd.token.Token;
import pillihuaman.com.basebd.help.AuditEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;



@AllArgsConstructor
@Document(collection = "user")
@Builder
@NoArgsConstructor
public class User implements UserDetails ,Serializable{

    private static final long serialVersionUID = 1L;
    @Id
    private ObjectId id;
    private AuditEntity auditEntity;
    private String alias;
    private List<System> idSystem;
    @Indexed(unique = true)
    private String email;
    private String mobilPhone;
    private String user;
    @Getter
    private String userName;
    private String userNameP;
    private String apiPassword;
    private String passwordP;
    @Getter
    private String password;
    private String salPassword;
    private boolean enabled;
    private String name;
    private String typeDocument;
    @Indexed(unique = true)
    private String numTypeDocument;
    private Role role;
    private List<Token> tokens;

    public User(User user) {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<System> getIdSystem() {
        return idSystem;
    }

    public void setIdSystem(List<System> idSystem) {
        this.idSystem = idSystem;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilPhone() {
        return mobilPhone;
    }

    public void setMobilPhone(String mobilPhone) {
        this.mobilPhone = mobilPhone;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNameP() {
        return userNameP;
    }

    public void setUserNameP(String userNameP) {
        this.userNameP = userNameP;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getPasswordP() {
        return passwordP;
    }

    public void setPasswordP(String passwordP) {
        this.passwordP = passwordP;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalPassword() {
        return salPassword;
    }

    public void setSalPassword(String salPassword) {
        this.salPassword = salPassword;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public String getNumTypeDocument() {
        return numTypeDocument;
    }

    public void setNumTypeDocument(String numTypeDocument) {
        this.numTypeDocument = numTypeDocument;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}