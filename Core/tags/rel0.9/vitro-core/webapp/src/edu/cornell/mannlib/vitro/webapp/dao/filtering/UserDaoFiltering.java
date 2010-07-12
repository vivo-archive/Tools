package edu.cornell.mannlib.vitro.webapp.dao.filtering;

/*
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import edu.cornell.mannlib.vitro.webapp.beans.*;
import edu.cornell.mannlib.vitro.webapp.dao.UserDao;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;
import net.sf.jga.fn.UnaryFunctor;

import java.util.List;

public class UserDaoFiltering extends BaseFiltering implements UserDao{

    private final UserDao innerDao;
    private final VitroFilters filters;

    public UserDaoFiltering(UserDao userDao, VitroFilters filters) {
        this.innerDao = userDao;
        this.filters = filters;
    }

    public List<User> getAllUsers() {
        return filter(innerDao.getAllUsers(),filters.getUserFilter());
    }

    public User getUserByURI(String URI) {
        User u = innerDao.getUserByURI(URI);
        if( u != null && filters.getUserFilter().fn(u))
            return u;
        else
            return null;
    }

    public User getUserByUsername(String username) {
        User u = innerDao.getUserByUsername(username);
        if( u != null && filters.getUserFilter().fn(u))
            return u;
        else
            return null;
    }

    public void updateUser(User user) {
        innerDao.updateUser(user);
    }

    public String insertUser(User user) {
        return innerDao.insertUser(user);
    }

    public void deleteUser(User user) {
        innerDao.deleteUser(user);
    }

    public List<String> getIndividualsUserMayEditAs(String userURI) {
        return innerDao.getIndividualsUserMayEditAs(userURI);
    }

}