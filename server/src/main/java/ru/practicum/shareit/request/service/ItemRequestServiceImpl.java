package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.StateException;
import ru.practicum.shareit.exception.util.Constants;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestResponse addRequestItem(ItemRequestRequest itemRequestRequest, Long authorId)
            throws NotFoundException {
        if (notExist(authorId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, authorId));
        }
        var author = userRepository.findById(authorId).get();
        var itemReq = itemRequestRepository.save(itemRequestMapper.fromDto(itemRequestRequest, author));
        var items = itemRepository.findAllByItemRequest(itemReq).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        return itemRequestMapper.toDto(itemReq, items);
    }

    @Override
    public List<ItemRequestResponse> getAllRequestItem(Integer from, Integer size, Long authorId) throws NotFoundException, StateException {
        if (notExist(authorId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, authorId));
        }
        if (from < 0) throw new RuntimeException("from не может быть меньше 0");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        var itemReqs = itemRequestRepository.findAll(pageable);
        var items = itemRepository.findAllByItemRequestIdNotNull();
        return itemReqs.stream()
                .filter(ir -> !ir.getAuthor().getId().equals(authorId))
                .map(ir -> itemRequestMapper.toDto(ir, items.stream()
                        .filter(i -> i.getItemRequest().getId().equals(ir.getId()))
                        .map(itemMapper::toDto).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponse> getAllOwnerRequestItem(Integer from, Integer size, Long authorId) throws NotFoundException, StateException {
        if (notExist(authorId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, authorId));
        }
        if (from < 0) throw new RuntimeException("from не может быть меньше 0");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        var itemReqs = itemRequestRepository.findAllByAuthorId(authorId, pageable);
        var items = itemRepository.findAllByItemRequestAuthorId(authorId);
        return itemReqs.stream()
                .map(ir -> itemRequestMapper.toDto(ir, items.stream()
                        .map(itemMapper::toDto).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponse getRequestItem(Long requestId, Long authorId) throws NotFoundException {
        if (notExist(authorId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, authorId));
        }
        var itemReq = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ITEM_NOT_FOUND, requestId)));
        var items = itemRepository.findAllByItemRequest(itemReq).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        return itemRequestMapper.toDto(itemReq, items);
    }

    private boolean notExist(Long userId) {
        return userRepository.findById(userId).isEmpty();
    }
}
