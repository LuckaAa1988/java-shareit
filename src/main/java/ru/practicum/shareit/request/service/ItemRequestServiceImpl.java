package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
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

    @Override
    public ItemRequestResponse addRequestItem(ItemRequestRequest itemRequestRequest, Long authorId)
            throws NotFoundException {
        if (notExist(authorId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, authorId));
        }
        var author = userRepository.findById(authorId).get();
        var itemReq = itemRequestRepository.save(ItemRequestMapper.INSTANCE.fromDto(itemRequestRequest, author));
        var items = itemRepository.findAllByItemRequest(itemReq).stream()
                .map(ItemMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.INSTANCE.toDto(itemReq, items);
    }

    @Override
    public List<ItemRequestResponse> getAllRequestItem(Integer from, Integer size, Long authorId) throws NotFoundException, StateException {
        if (notExist(authorId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, authorId));
        }
        if (size != null && size == 0) {
            throw new StateException("размер не может быть 0");
        }
        var itemReqs = itemRequestRepository.findAll(from == null ? 0 : from, size == null ? Integer.MAX_VALUE : size);
        var items = itemRepository.findAll();
        return itemReqs.stream()
                .filter(ir -> !ir.getAuthor().getId().equals(authorId))
                .map(ir -> ItemRequestMapper.INSTANCE.toDto(ir, items.stream()
                        .filter(i -> i.getItemRequest() != null && i.getItemRequest().getId().equals(ir.getId()))
                        .map(ItemMapper.INSTANCE::toDto).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponse> getAllOwnerRequestItem(Integer from, Integer size, Long authorId) throws NotFoundException, StateException {
        if (notExist(authorId)) {
            throw new NotFoundException(String.format(Constants.USER_NOT_FOUND, authorId));
        }
        if (size != null && size == 0) {
            throw new StateException("размер не может быть 0");
        }
        var itemReqs = itemRequestRepository.findAllByAuthor(authorId,
                from == null ? 0 : from,
                size == null ? Integer.MAX_VALUE : size);
        var items = itemRepository.findAll();
        return itemReqs.stream()
                .map(ir -> ItemRequestMapper.INSTANCE.toDto(ir, items.stream()
                        .filter(i -> i.getItemRequest() != null && i.getItemRequest().getId().equals(ir.getId()))
                        .map(ItemMapper.INSTANCE::toDto).collect(Collectors.toList())))
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
                .map(ItemMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.INSTANCE.toDto(itemReq, items);
    }

    private boolean notExist(Long userId) {
        return userRepository.findById(userId).isEmpty();
    }
}
