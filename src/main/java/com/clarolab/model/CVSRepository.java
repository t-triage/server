package com.clarolab.model;

import lombok.*;
import org.eclipse.jgit.util.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.clarolab.util.Constants.TABLE_CVS_REPOSITORY;

@Entity
@Table(name = TABLE_CVS_REPOSITORY)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CVSRepository extends Entry{

    private String url;

    @Column(name = "local_path")
    private String localPath;

    private String username;
    private String password;
    private String branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "last_read")
    private long lastRead;

    @Type(type = "org.hibernate.type.TextType")
    private String packageNames;

    @Builder
    public CVSRepository(Long id, boolean enabled, long updated, long timestamp, String url, String localPath, String username, String password, String branch, Product product, long lastRead, String packageNames) {
        super(id, enabled, updated, timestamp);
        this.url = url;
        this.localPath = localPath;
        this.username = username;
        this.password = password;
        this.branch = branch;
        this.product = product;
        this.lastRead = lastRead;
        this.packageNames = packageNames;
    }

    public List<String> getPackages() {
        List<String> result = new ArrayList<>();
        if (!StringUtils.isEmptyOrNull(packageNames)) {
            String[] pNames = packageNames.split(",");
            for (int i = 0; i < pNames.length; i++){
                result.add(pNames[i].trim().replace(".","/"));
            }
        }
        return result;
    }
}
