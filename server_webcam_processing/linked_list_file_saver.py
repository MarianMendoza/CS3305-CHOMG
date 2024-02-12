class Node(object):
    '''
    Linked List Node
    '''
    def __init__(self, frame, next_node, previous_node) -> None:
        self.frame = frame
        self.next_node = next_node   
        self.previousNode = previous_node

    def set_frame(self, frame):
        self.frame = frame
    
    def get_frame(self):
        return self.frame
    
    def get_next_node(self):
        return self.next_node    
    
    def set_next_node(self, next_node):
        self.next_node = next_node

    def get_previous_node(self):
        return self.previous_node    
    
    def set_previous_node(self, previous_node):
        self.previous_node = previous_node
    
class LinkedList(object):
    '''
    Linked list representation
    '''
    amount_of_nodes = 0
    def __init__(self) -> None:
        self.start = Node(None, None, None)
        self.end = Node(None, None, None)

        self.start.set_next_node(self.end)
        self.end.set_previous_node(self.start)

    def return_list_of_linked_list(self):
        linked_list = []
        current_node=self.start.get_next_node()
        while current_node.get_next_node() is not None:
            linked_list += [current_node.get_frame()]
            current_node = current_node.get_next_node()
        return linked_list
    
    def add_frame(self, frame):
        if LinkedList.amount_of_nodes < 900: 
            self.__add_frame_at_end(frame)
        else:
            self.__remove_first_frame_and_add_new_frame_at_end(frame)

    def __add_frame_at_end(self, frame):
        last_node = self.end.get_previous_node()
        new_node = Node(frame, last_node, self.end)
        self.end.set_previous_node(new_node)
        last_node.set_next_node(new_node)
        LinkedList.amount_of_nodes += 1
    
    def __remove_first_frame_and_add_new_frame_at_end(self, frame):
        first_node = self.start.get_next_node()
        second_node = first_node.get_next_node()
        self.start.set_next_node(second_node)
        second_node.set_previous_node(self.start)

        last_node = self.end.get_previous_node()
        first_node.set_frame(frame)
        first_node.set_next_node(self.end)
        first_node.set_previous_node(self.last_node)
        self.end.set_previous_node(first_node)
        last_node.set_next_node(first_node)