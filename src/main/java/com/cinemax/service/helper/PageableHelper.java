package com.cinemax.service.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageableHelper {
	public Pageable getPageable(
				int page,
				int size,
				String sort,
				String type) {
		Sort.Direction direction = Sort.Direction.fromString(type);
		return PageRequest.of(page, size, Sort.by(direction, sort));
	}
}
