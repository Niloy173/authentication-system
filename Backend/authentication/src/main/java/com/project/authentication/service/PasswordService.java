package com.project.authentication.service;

import com.project.authentication.dto.request.ChangePasswordRequest;
import com.project.authentication.dto.request.ForgotPasswordRequest;
import com.project.authentication.dto.request.ResetPasswordRequest;
import com.project.authentication.entity.User;

public interface PasswordService {

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request, User currentUser);
}
