package com.example.coursework.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Setter
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column
    private boolean enabled;
    @Column
    private String role;

    @OneToMany(mappedBy = "owner", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Video> channel;
    public void add_to_channel(Video video)
    {
        channel.add(video);
    }
    public void remove_from_channel(Video video)
    {
        channel.remove(video);
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "liked_videos",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "videoId")
    )
    private List<Video> likedVideos;

    public void addToLiked(Video video){
        this.likedVideos.add(video);
    }

    public void removeFromLiked(Video video){
        this.likedVideos.remove(video);
    }

    public List<Video> getChannel() {
        return channel;
    }

    public List<Video> getLikedVideos() {
        return likedVideos;
    }

    /**
     * returns authority of the user for security checks
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        authorities.add(authority);

        return authorities;

    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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
        return enabled;
    }

}