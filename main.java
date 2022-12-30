// Vincent Revel Aldisa_205150200111019

package VaknasiCOVID_19;

import java.util.Scanner;

public class Running {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int index = 0;
        String [] arrInput = new String[index];
        while(in.hasNextLine()){
            String line = in.nextLine();
            if(line.length() == 0){
                break;
            }else{
                index++;
                String [] oldArray = arrInput;
                arrInput = new String[index];
                for(int i = 0; i < oldArray.length; i++){
                    arrInput[i] = oldArray[i];
                }
                arrInput[index-1] = line;
            }
        }  
        RuangAntre rA = new RuangAntre(Integer.parseInt(arrInput[0]));
        DaftarTunggu dT = new DaftarTunggu();
        for(int i = 1; i < arrInput.length; i++){
            String [] splitLine = arrInput[i].split(" ");
            if(splitLine[0].equals("BARU")){
                Member data = new Member(splitLine[1], Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]));
                if(data.getTensi() >= 180){
                    System.out.printf("TOLAK %s %s %s\n", data.getNama(), data.getStatus_Usia(), data.getStatus_Vaksin());
                }else{
                    if(data.getPriority() == 1){
                        if(rA.isEmpty()){
                            rA.enQueue(data);
                            System.out.printf("ANTRE %s %s\n", data.getNama(), data.getStatus_Usia());
                        }else if(!rA.isEmpty() && !rA.isFull()){
                            rA.enQueueLansia(data);
                            System.out.printf("ANTRE %s %s\n", data.getNama(), data.getStatus_Usia());
                        }else if(rA.isFull()){
                            if(rA.LansiaCount() == rA.getCount()){
                                dT.enQueueLansia(new NodeDT(data));
                                System.out.printf("TUNGGU %s %s\n", data.getNama(), data.getStatus_Usia());
                            }else{
                                Member temp = rA.exchange(data);
                                dT.enQueue(new NodeDT(temp));
                                System.out.printf("ANTRE %s %s TUNGGU %s\n", data.getNama(), data.getStatus_Usia(), temp.getNama());
                            }
                        }
                    }else if(data.getPriority() == 0){
                        if(rA.isEmpty()){
                            rA.enQueue(data);
                            System.out.printf("ANTRE %s %s\n", data.getNama(), data.getStatus_Usia());
                        }else if(!rA.isEmpty() && !rA.isFull()){
                            rA.enQueue(data);
                            System.out.printf("ANTRE %s %s\n", data.getNama(), data.getStatus_Usia());
                        }else if(rA.isFull()){
                            dT.enQueue(new NodeDT(data));
                            System.out.printf("TUNGGU %s %s\n", data.getNama(), data.getStatus_Usia());
                        }
                    }
                }
            }else if(splitLine[0].equals("UKURAN")){
               Member [] overArr = rA.arrayExtend(Integer.parseInt(splitLine[1]));
               if(overArr != null){
                   for(int j = 0; j < overArr.length; j++){
                       if(overArr[j].getPriority() == 1){
                           dT.enQueueLansia(new NodeDT(overArr[j]));
                       }else{
                           dT.enQueue(new NodeDT(overArr[j]));
                       }
                   }
               }else{
                    if(!dT.isEmpty()){
                        Member temp = dT.getFront();
                        dT.deQueue();
                        if(temp.getPriority() == 1){
                            rA.enQueueLansia(temp);
                        }else{
                            rA.enQueue(temp);
                        }
                    } 
               }
            }else if(splitLine[0].equals("SELESAI")){
                if(!rA.isEmpty()){
                    System.out.print("SELESAI_VAKSIN ");
                    for(int j = 0; j < Integer.parseInt(splitLine[1]); j++){
                        rA.deQueue();
                        if(!dT.isEmpty()){
                            Member temp = dT.getFront();
                            dT.deQueue();
                            if(temp.getPriority() == 1){
                                rA.enQueueLansia(temp);
                            }else{
                                rA.enQueue(temp);
                            }
                        }
                    }
                    System.out.println();
                }else{
                    System.out.println("ANTRE KOSONG");
                }
            }else if(splitLine[0].equals("SELESAI_NAMA")){
                if(!rA.isEmpty()){
                    System.out.print("SELESAI_VAKSIN ");
                    rA.deQueueByName(splitLine[1]);
                    if(!dT.isEmpty()){
                        Member temp = dT.getFront();
                        dT.deQueue();
                        if(temp.getPriority() == 1){
                            rA.enQueueLansia(temp);
                        }else{
                            rA.enQueue(temp);
                        }
                    }
                    System.out.println();
                }else{
                    System.out.println("ANTRE KOSONG");
                }
            }else if(splitLine[0].equals("SKIP")){
               Member temp = rA.skip(splitLine[1]);
               if(temp != null){
                   if(temp.getPriority() == 1){
                       dT.enQueueLansia(new NodeDT(temp));
                   }else{
                       dT.enQueue(new NodeDT(temp));
                   }
                   Member frontDT = dT.getFront();
                   dT.deQueue();
                   if(frontDT.getPriority() == 1){
                       rA.enQueueLansia(frontDT);
                   }else{
                       rA.enQueue(frontDT);
                   }
               }
            }else if(splitLine[0].equals("STATUS")){
                rA.printData();
                dT.printData();
            }
        
        }
    }
}

class Member {
    String nama, status_Usia, status_Vaksin;
    int usia, tensi, priority = 0;
    
    public Member(String nama, int usia, int tensi){
        this.nama = nama;
        this.usia = usia;
        this.tensi = tensi;
        if(this.usia >= 60){
            this.status_Usia = "LANSIA";
            priority++;
        }else{
            this.status_Usia = "BUKAN_LANSIA";
        }
        if(this.tensi >= 180){
            this.status_Vaksin = "TENSI_TIDAKBOLEH_DIVAKSIN";
        }else{
            this.status_Vaksin = "TENSI_BOLEH_DIVAKSIN";
        }
    }

    public String getNama() {
        return this.nama;
    }
    public String getStatus_Usia() {
        return this.status_Usia;
    }
    public String getStatus_Vaksin() {
        return this.status_Vaksin;
    }
    public int getUsia() {
        return this.usia;
    }
    public int getTensi() {
        return this.tensi;
    }
    public int getPriority() {
        return this.priority;
    }
}

class DaftarTunggu {
    NodeDT front;
    DLinkedList dll;

    public DaftarTunggu(){
        dll = new DLinkedList();
        front = dll.head;
    }
    // Counter
    public boolean isEmpty(){
        return dll.isEmpty();
    }

    public void makeEmpty(){
        dll.makeEmpty();
    }

    public int getCount(){
        return dll.size;
    }

    public Member getFront(){
        return front.data;
    }

    // Function
    public void deQueue(){
        if(!isEmpty()){
            dll.removeFirst();
            front = dll.head;
        }else{
            return;
        }
    }
    
    public void enQueue(NodeDT penerima){
        dll.addLast(penerima);
        front = dll.head;
    }
    
    public void enQueueLansia(NodeDT penerima){
        NodeDT tmp = dll.head;
        while(tmp != null){
            if(tmp.data.getStatus_Usia().equals("BUKAN_LANSIA")){
                dll.insertBefore(tmp, penerima);
            }
            tmp = tmp.next;
        }
        System.out.printf("TUNGGU %s %s\n", penerima.data.getNama(), penerima.data.getStatus_Usia());        
        front = dll.head;
    }

    public void printData(){
        dll.printToLast();
    }
}

class RuangAntre {
    Member [] queue;
    int front, rear;
    int antrianCount;

    public RuangAntre(){}
    public RuangAntre(int roomSize){
        queue = new Member[roomSize];
        front = 0;
        rear = -1;
        antrianCount = 0;
    }
    
    // Behaviour
    public Member[] arrayExtend(int newSize){
        Member [] overArray =  null;
        Member [] oldArray = queue;
        if(newSize < queue.length){
            int index = 0;
            overArray = new Member [queue.length - newSize];
            for(int i = newSize; i < queue.length; i++){
                overArray [index] = queue[i]; 
            }
            queue = new Member [newSize];
            for(int i = 0; i < queue.length; i++){
                queue[i] = oldArray[i];
            }
            antrianCount = newSize;
            rear = newSize-1;
        }else if(newSize > queue.length){
            queue = new Member [newSize];
            for(int i = 0; i < oldArray.length; i++){
                queue[i] = oldArray[i];
            }
        }
        System.out.println("SUKSES UBAH " + oldArray.length + " " + newSize);
        return overArray;
    }
    
    // Enqueue
    public void enQueueLansia(Member penerima){
        int index = 0;
        for(int i = 0; i < antrianCount; i++){
            if(queue[i].getPriority() == 0){
                index = i;
                break;
            }
        }
        for(int i = antrianCount-1; i > index; i--){
            queue[i] = queue[i-1];
        }
        queue[index] = penerima;
        rear++;
        antrianCount++;
        
    }

    public void enQueue(Member penerima){
        queue[++rear] = penerima;  
        antrianCount++;
    }
    
    // Dequeue
    public void deQueue(){
        if(!isEmpty()){
            String namaSelesai = queue[front].getNama();
            for(int i = 0; i < queue.length-1; i++){
                queue[i] = queue[i+1];
            }
            System.out.print(namaSelesai + " ");
            queue[rear--] = null;
            antrianCount--;
        }else{
            System.out.print("ANTRE KOSONG");
        }
    }

    public void deQueueByName(String nama){
        int index = 0;
        for(int i = 0; i < antrianCount; i++){
            if(queue[i].getNama().equals(nama)){
                index = i;
                break;
            }
        }
        String namaPenerima = queue[index].getNama();
        for(int i = index; i < antrianCount-1; i++){
            queue[i] = queue[i+1];
        }
        System.out.print(namaPenerima);
        queue[rear--] = null;
        antrianCount--;
    }
    
    public Member skip(String diSkip){
        int index = -1;
        // Search for data (if found index >-1)
        for(int i = 0; i < antrianCount; i++){
            if(queue[i].getNama().equals(diSkip)){
                index = i;
                break;
            }
        }
        if(index == -1){
            System.out.println("SKIP GAGAL");
            return null;
        }else{
            // Move data array to the left
            Member temp  = queue[index];
            for(int i = index; i < antrianCount-1; i++){
                queue[i] = queue[i+1];
            }
            queue[rear--] = null;
            antrianCount --;
            System.out.println("SKIP SUKSES");
            return temp;
        }
    }

    public Member exchange(Member penerima){
        Member temp = queue[antrianCount-1];
        queue[antrianCount-1] = null;
        enQueueLansia(penerima);
        rear--;
        antrianCount--;
        return temp;
    }

    // Counter
    
    public boolean isEmpty(){
        return antrianCount == 0;
    }
    
    public boolean isFull(){
        return antrianCount == queue.length;
    }
    
    public int getCount(){
        return this.antrianCount;
    }
    
    public Member getFront(){
        return this.queue[front];
    }
    
    public Member getRear(){
        return this.queue[rear];
    }
    
    public int LansiaCount(){
        int index = 0;
        for(int i = 0; i < antrianCount; i++){
            if(queue[i].getPriority() == 1){
                index++;
            }
        }
        return index;
    }
    
    // Display
    public void printData(){
        System.out.print("DAFTAR_ANTRE ");
        if(!isEmpty()){
            for(int i = 0; i < antrianCount; i++){
                System.out.printf("%s^%s^%d ", queue[i].getNama(), queue[i].getStatus_Usia(), queue[i].getTensi());
            }
        }else{
            System.out.print("-");
        }
        System.out.println();
    }
}

class DLinkedList {
    NodeDT head, tail;
    int size;

    DLinkedList(){
        makeEmpty();
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public void makeEmpty(){
        head = tail = null;
        size = 0;
    }

    public int getSize(){
        return this.size;
    }
   
    // Operasi Dasar
    public void addFirst(NodeDT add){
        if(isEmpty()){
            head = tail = add;
        }else{
            add.next = head;
            head.prev = add;
            head = add;
            size++;
        }
    }

    public void addLast(NodeDT add){
        if(isEmpty()){
            head = tail = add;
        }else{
            add.prev = tail;
            tail.next = add;
            tail = add;
        }
        size++;
    }

    public void insertAfter(String after, NodeDT add){
        NodeDT curr = head;
        if(isEmpty()){
            head = tail = add;
            return;
        }
        while (curr != null){
            if(curr.data.getNama().equals(after)){
                if(curr == tail){
                    addLast(add);
                    break;
                }else{
                    add.next = curr.next; 
                    curr.next.prev = add;  
                    curr.next = add;
                    add.prev = curr;
                    size++;
                }
            }
            curr = curr.next;
        }
    }

    public void insertBefore(NodeDT before, NodeDT add){
        NodeDT curr = head;
        if(isEmpty()){
            head = tail = add;
            return;
        }
        while (curr != null){
            if(curr == before){
                if(curr == head){
                    addFirst(add);
                    break;
                }else{
                    add.prev = curr.prev; 
                    curr.prev.next = add;  
                    curr.prev = add;
                    add.next = curr;
                    size++;
                    break;
                }
            }
            curr = curr.next;
        }
    }

    public void removeFirst(){
        if(isEmpty()){
            return;
        }else if(head.next == null){
            head = tail = null;
            size = 0;
        }else if(!isEmpty()){
            head = head.next;
            head.prev = null;
            size--;
        }
    }

    public void removeLast(){
        if(isEmpty()){
            return;
        }else if(tail.prev == null){
            head = tail = null;
            size = 0;
        }else if(!isEmpty()){
            tail = tail.prev;
            tail.next = null;
            size--;
        }
    }

    // Print    
    public void printToLast(){
        System.out.print("DAFTAR_TUNGGU ");
        NodeDT p = head;
        if(isEmpty()){
            System.out.print("-");
        }else{
            while(p != null){
                System.out.printf("%s^%s^%d ", p.data.getNama(), p.data.getStatus_Usia(), p.data.getTensi());
                p = p.next;
            }
        }
        System.out.println();
    }
}

class NodeDT {
    NodeDT next, prev;
    Member data;
    NodeDT(Member theData){
        this.data = theData; 
    }
}