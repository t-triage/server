/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.view.feature;

import com.clarolab.view.View;
import com.clarolab.view.feature.login.ExternalLoginView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeatureListView implements View {

    private Boolean internalLoginEnabled;
    private Set<ExternalLoginView> externalLoginURIs;

}
