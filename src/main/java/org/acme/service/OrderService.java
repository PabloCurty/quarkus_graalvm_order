package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.acme.client.CustomerClient;
import org.acme.client.ProductClient;
import org.acme.dto.CustomerDTO;
import org.acme.dto.OrderDTO;
import org.acme.entity.OrderEntity;
import org.acme.repository.OrderRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject
    private OrderRepository orderRepository;

    @Inject
    @RestClient
    private CustomerClient customerClient;

    @Inject
    @RestClient
    private ProductClient productClient;

    public List<OrderDTO> getAllOrders(){
        List<OrderDTO> ordersDTO = new ArrayList<>();
        orderRepository.findAll().stream().forEach(item -> {
            ordersDTO.add(mapEntitytoDTO(item));
        });
        return ordersDTO;
    }

    private OrderDTO mapEntitytoDTO(OrderEntity orderEntity){
        OrderDTO orderDTO = new OrderDTO(
                orderEntity.getCustomerID(),
                orderEntity.getCustomerName(),
                orderEntity.getProductId(),
                orderEntity.getOrderValue());
        return orderDTO;
    }

    public void saveNewOrder(OrderDTO orderDTO){
        CustomerDTO customerDTO = customerClient.getCustomerById(orderDTO.getCustomerId());

        if(Arrays.equals(customerDTO.getName(), orderDTO.getCustomerName())
                && productClient.getProductById(orderDTO.getProductId()) != null){
            orderRepository.persist(mapDTOtoEntity(orderDTO));
        }else {
            throw new NotFoundException();
        }
    }

    private OrderEntity mapDTOtoEntity(OrderDTO orderDTO) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCustomerID(orderDTO.getCustomerId());
        orderEntity.setCustomerName(orderDTO.getCustomerName());
        orderEntity.setProductId(orderDTO.getProductId());
        orderEntity.setOrderValue(orderDTO.getOrderValue());

        return orderEntity;
    }
}
