package com.example.coursework.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long videoId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private long likes = 0;
    @Column(nullable = false)
    private String category;
    @JsonIgnore
    private String sourcePath;

    /**
     * @param sourcePath path to the videofile associated with this Video object
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @ManyToOne()
    @JoinColumn(name="user_id")
    private User owner;

    @ManyToMany(mappedBy = "likedVideos", fetch = FetchType.EAGER)
    private List<User> usersLiked;

    public synchronized void addLike(User user) {
        this.usersLiked.add(user);
        this.likes +=1;
    }
    public synchronized void removeLike(User user) {
        this.usersLiked.remove(user);
        this.likes -=1;
    }
    public void addUser(User user)
    {
        owner = user;
    }

    public String getName() {
        return name;
    }
}
