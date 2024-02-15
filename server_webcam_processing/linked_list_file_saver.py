class Node(object):
    '''
    Linked List Node
    '''
    def __init__(self, frame, next_node, previous_node) -> None:
        self.frame = frame
        self.next_node = next_node   
        self.previous_node = previous_node

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
        self.empty = True

    def get_list_of_frames_in_linked_list(self):
        '''
        Returns list of frames stored in list
        '''
        linked_list = []
        current_node=self.start.get_next_node()
        while current_node != self.end:
            linked_list += [current_node.get_frame()]
            current_node = current_node.get_next_node()
        return linked_list
    
    def add_frame(self, frame):
        '''
        Adds frame to linked list
        '''
        if LinkedList.amount_of_nodes < 600: 
            self.__add_frame_at_end(frame)
        else:
            self.__remove_first_frame_and_add_new_frame_at_end(frame)
        self.empty = False

    def __add_frame_at_end(self, frame):
        '''
        Create and insert a node into the end of the linked list with the inputted frame as its stored data
        '''
        last_node = self.end.get_previous_node()
        new_node = Node(frame, self.end, last_node)
        self.end.set_previous_node(new_node)
        last_node.set_next_node(new_node)
        LinkedList.amount_of_nodes += 1
    
    def __remove_first_frame_and_add_new_frame_at_end(self, frame):
        '''
        Disconnects pre-existing frame from list and re-inserts it as end of list with new data
        '''

        # Disconnect first node from start of linked list
        first_node = self.start.get_next_node()
        second_node = first_node.get_next_node()
        self.start.set_next_node(second_node)
        second_node.set_previous_node(self.start)

        # Overwrite node and insert it into end of linked list
        last_node = self.end.get_previous_node()
        first_node.set_frame(frame)
        first_node.set_next_node(self.end)
        first_node.set_previous_node(last_node)
        self.end.set_previous_node(first_node)
        last_node.set_next_node(first_node)

    def clear_linked_list(self):
        self.start.set_next_node(self.end)
        self.end.set_previous_node(self.start)
        self.empty = True

    def is_empty(self):
        '''
        Returns if list is empty or not
        '''
        return self.empty