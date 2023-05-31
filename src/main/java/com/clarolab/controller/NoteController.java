/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.controller;


import com.clarolab.dto.NoteDTO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.clarolab.util.Constants.API_NOTE_URI;

@RequestMapping(API_NOTE_URI)
@Api(value = "Notes", description = "Here you will find all those operations related with Note entities", tags = {"Notes"})
public interface NoteController extends BaseController<NoteDTO> {


}
