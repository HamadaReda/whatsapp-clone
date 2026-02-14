package com.wa.whatsappclone.user;

import com.wa.whatsappclone.chat.Chat;
import com.wa.whatsappclone.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_user_keycloak_id", columnNames = "keycloak_id"),
            @UniqueConstraint(name = "uk_user_email", columnNames = "email")
        })
@NamedQuery(name = UserConstants.FIND_USER_BY_KEYCLOAK_ID,
            query = "SELECT u FROM User u WHERE u.keycloakId = :keycloakId")
@NamedQuery(name = UserConstants.FIND_USER_BY_EMAIL,
            query = "SELECT u FROM User u WHERE u.email = :email")
@NamedQuery(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF,
            query = "SELECT u FROM User u WHERE u.id != :publicId")
//@NamedQuery(name = UserConstants.FIND_USER_BY_PUBLIC_ID,
//            query = "SELECT u FROM User u WHERE u.id = :publicId")
public class User extends BaseAuditingEntity {

    private static final int LAST_ACTIVE_INTERVAL = 5;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "keycloak_id", nullable = false)
    private String keycloakId;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String email;

    private LocalDateTime lastSeen;

    @OneToMany(mappedBy = "sender")
    private List<Chat> chatsAsSender;

    @OneToMany(mappedBy = "recipient")
    private List<Chat> chatsAsRecipient;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public boolean isUserOnline() {
        return lastSeen != null
                && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));
    }
}
