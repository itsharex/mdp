package top.mddata.open.query.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @author henhen6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocIdsParam {

    @NotNull
    private Collection<Long> docIds;
}
