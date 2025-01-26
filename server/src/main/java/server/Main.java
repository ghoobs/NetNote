/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Optional;
import java.util.Scanner;

import static org.apache.commons.lang3.ArrayUtils.add;

@SpringBootApplication
@EntityScan(basePackages = {"commons", "server"})
public class Main {

    public static void main(String[] args) {
//        System.out.println("Enter port number: (default is 8080)");
//        var ln = new Scanner(System.in).nextLine();
//
//        Optional<Integer> port = Optional.empty();
//        try {
//            port = Optional.of(Integer.parseInt(ln));
//        } catch (NumberFormatException e) {
//            System.out.println("Invalid port number");
//            System.out.println("Continuing with 8080");
//        }
//
//        if (port.isPresent()){
//            args = add(args, "--server.port=" + port.get());
//        }

        SpringApplication.run(Main.class, args);
    }
}