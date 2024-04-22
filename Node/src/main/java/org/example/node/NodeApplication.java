package org.example.node;

import org.example.node.QueriesHandling.CollectionQueriesHandler;
import org.example.node.QueriesHandling.DatabaseQueriesHandler;
import org.example.node.QueriesHandling.QueryDispatcher;
import org.example.node.Transaction.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.util.Map;

@SpringBootApplication
@EnableAsync
public class NodeApplication {

	public static void main(String[] args) {

		SpringApplication.run(NodeApplication.class, args);
//		QueryDispatcher qd = new QueryDispatcher();
//		Object res = qd.dispatchQuery("Majors.IT.find({\"name\":\"mech\"})"); // working good
//
//		for (Map<String, Object> map : ((Response) res).getDocuments()) {
////			// Iterate over the map entries and print key-value pairs
//			for (Map.Entry<String, Object> entry : map.entrySet()) {
//				System.out.println(entry.getKey() + ": " + entry.getValue());
//			}
//			System.out.println(); // Add a newline between maps
//		}
//		qd.dispatchQuery("buses.users.insert({\"name\":\"haneen\", \"age\":27})");
		////        doc.handleQuery("cars.users.findAll()"); // working good
//		qd.dispatchQuery("cars.users.findAll()");
////        doc.handleQuery("cars.users.update(\"ac9d205ddfffc780a5598967918590058ad6471edaf30ca549743e0d263d856a\", \"{\\\"name\\\":\\\"Mahmoud\\\"}\", \"{\\\"name\\\":\\\"Mohammed\\\"}\")");
//		qd.dispatchQuery("buses.users.update(\"00121d8b-9246-4c77-8f65-69ecb321dcdf\", \"{\\\"age\\\":\\\"20\\\"}\")");
//		qd.dispatchQuery("buses.users.createIndex.age");
		////        doc.handleQuery("cars.users.delete(f23122b633db688ff56cebc780b13a25f89d6011394e2c6e45326148b30ee35c)"); // working good
//		qd.dispatchQuery("buses.users.delete(1f2d65d0-ccbf-4acb-9ed2-50f419eae45b)");

		//		qd.dispatchQuery("tabels.hi.update(85bb48f0-794a-4926-b578-2978d43c01c6, {age:2}));

	}}


