package io.electrica.stl.repository;

import io.electrica.stl.model.AwsIamAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwsIamAuthorizationRepository extends JpaRepository<AwsIamAuthorization, Long> {
}
